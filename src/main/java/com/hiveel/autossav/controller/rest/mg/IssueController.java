package com.hiveel.autossav.controller.rest.mg;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hiveel.autossav.manager.PushManager;
import com.hiveel.autossav.model.conf.SendGridEmailTemplate;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.*;
import com.hiveel.core.log.util.LogUtil;
import com.hiveel.core.util.SendGridEmailUtil;
import com.hiveel.core.util.ThreadUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;

@RestController
public class IssueController {
    @Autowired
    private IssueService service;
    @Autowired
    private PersonService personService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleGroupService vehicleGroupService;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private OdometerService odometerService;
    @Autowired
    private ExamService examService;
    @Autowired
    private VehicleDriverRelateService vehicleDriverRelateService;
    @Autowired
    private PushManager pushManager;
    @Autowired
    private SendGridEmailUtil emailUtil;

    @PostMapping({"/mg/issue"})
    public Rest<Long> save(Issue e) throws ParameterException {
        ParameterExceptionUtil.verify("issue.vehicle", e.getVehicle()).isNotNull();
        ParameterExceptionUtil.verify("issue.vehicle.id", e.getVehicle().getId()).isPositive();
        ParameterExceptionUtil.verify("issue.driver", e.getDriver()).isNotNull();
        ParameterExceptionUtil.verify("issue.driver.id", e.getDriver().getId()).isPositive();
        ParameterExceptionUtil.verify("issue.name", e.getName()).isLengthIn(1, 50);
        ParameterExceptionUtil.verify("issue.odometer", e.getOdometer()).isPositive();
        service.save(e);
        ThreadUtil.run(()->{
            Odometer odometer = new Odometer.Builder().set("relateId", e.getId()).set("type", OdometerType.ISSUE).set("vehicle", e.getVehicle()).set("mi", e.getOdometer()).set("date", e.getApptMinDate()).build();
            odometerService.save(odometer);
        	Vehicle vehicle = e.getVehicle();
        	vehicle.setOdometer(e.getOdometer());
        	vehicleService.update(vehicle);
        });
        return Rest.createSuccess(e.getId());
    }

    @DeleteMapping("/mg/issue/{id}")
    public Rest<Boolean> delete(Issue e) throws ParameterException {
        ParameterExceptionUtil.verify("issue.id", e.getId()).isPositive();
        boolean success = service.delete(e);
        return Rest.createSuccess(success);
    }

    @PutMapping("/mg/issue/{id}")
    public Rest<Boolean> update(Issue e) throws ParameterException {
        ParameterExceptionUtil.verify("issue.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("issue.vehicle | driver | name | apptMinDate | apptMaxDate | status | odometer | lon | lat",
                e.getVehicle(), e.getDriver(), e.getName(), e.getApptMinDate(), e.getApptMaxDate(), e.getStatus(), e.getOdometer(), e.getLon(), e.getLat()).atLeastOne().isNotEmpty();
        boolean success = service.update(e);
        if (success && e.getStatus() == IssueStatus.QUOTED) updateVehicleStatusToProblem(e);
        if (success && e.getStatus() == IssueStatus.COMPLETE) updateVehicleStatusByRelate(e);
        if (e.getOdometer() != null) updateVehicleOdometer(e);
        if (success) pushAfterUpdateStatus(e);
        return Rest.createSuccess(success);
    }
    
    private void updateVehicleStatusByRelate(Issue e) {
    	ThreadUtil.run(()->{
    		Issue data = service.findById(e);
            SearchCondition searchCondition=new SearchCondition();
            searchCondition.setOffDate("");
            List<VehicleDriverRelate> list = vehicleDriverRelateService.findByDriverAndVehicle(searchCondition, data.getDriver(), data.getVehicle());
            Vehicle vehicle = data.getVehicle();
            if (list.size() == 1) vehicle.setStatus(VehicleStatus.ACTIVE);            
            else vehicle.setStatus(VehicleStatus.INACTIVE);
            vehicleService.update(vehicle);
    	});
    }
    
    private void updateVehicleOdometer(Issue e) {
        ThreadUtil.run(()->{
        	Odometer data = odometerService.findByIssue(new SearchCondition(), e);
        	data.setMi(e.getOdometer());
        	odometerService.update(data);   
        	Vehicle vehicle = data.getVehicle();
        	vehicle.setOdometer(e.getOdometer());
        	vehicleService.update(vehicle);
        });
    }

    private void updateVehicleStatusToProblem(Issue e) {
    	ThreadUtil.run(()->{
            Vehicle vehicle = service.findById(e).getVehicle();
            vehicle.setStatus(VehicleStatus.PROBLEM);
            vehicleService.update(vehicle);
    	});
    }

    @PutMapping("/mg/issue/{id}/status")
    public Rest<Boolean> updateStatus(@RequestAttribute("loginPerson") Person loginPerson, Issue e) throws ParameterException {
        ParameterExceptionUtil.verify("issue.id", e.getId()).isPositive();
        if (loginPerson.getType() == PersonType.VE) e.setStatus(IssueStatus.CONFIRM);
        else if (loginPerson.getType() == PersonType.AS) ParameterExceptionUtil.verify("status", e.getStatus().toString()).beContainedIn(Arrays.asList("QUOTED", "SCHEDULED", "COMPLETE"));
        boolean success = service.update(e);
        if (success && e.getStatus() == IssueStatus.QUOTED)  updateVehicleStatusToProblem(e);
        if (success && e.getStatus() == IssueStatus.COMPLETE) updateVehicleStatusByRelate(e);
        if (success) pushAfterUpdateStatus(e);
        return Rest.createSuccess(success);
    }

    public void pushAfterUpdateStatus(Issue e) {
        ThreadUtil.run(() -> {
            Issue inDb = service.findById(e);
            Person person = inDb.getDriver();
            Vehicle vehicle = vehicleService.findById(inDb.getVehicle());
            e.setVehicle(vehicle);
            PushRecord pushRecord = new PushRecord.Builder().set("person", person).build();
            pushRecord.setRelateId(e.getId());
            pushRecord.addPayLoad("vehicle", vehicle).addPayLoad("issue", e);
            //确认issue维修事件的时间 触发推送给司机
            if (e.getApptMaxDate() != null || e.getApptMinDate() != null) {
                pushRecord.setType(PushRecordType.ISSUE_UPDATE_APPT);
                pushManager.pushAndSaveRecord(pushRecord);
                sendMail(inDb, person);
            }
            //车队已经通过isuue报价 所有As 和 VE权限用户收到推送
            if (e.getStatus() != null && e.getStatus() == IssueStatus.CONFIRM) {
                pushRecord.setType(PushRecordType.WS_ISSUE_CONFIRM);
                pushManager.pushAll2WebSocket(pushRecord, PersonType.AS);
                pushManager.pushAll2WebSocket(pushRecord, PersonType.VE);
            }
            //issue事件二次报价提醒 给全部 VE权限的用户推送一条信息
            if (e.getStatus() != null && e.getStatus() == IssueStatus.QUOTED) {
                pushRecord.setType(PushRecordType.WS_ISSUE_QUOTE);
                pushManager.pushAll2WebSocket(pushRecord, PersonType.VE);
            }
            //issue事件已经完成 给全部 VE权限的用户推送一条信息
            if (e.getStatus() != null && e.getStatus() == IssueStatus.COMPLETE) {
                pushRecord.setType(PushRecordType.WS_ISSUE_COMPLETE);
                pushManager.pushAll2WebSocket(pushRecord, PersonType.VE);
            }
        });
    }


    private void sendMail(Issue issue, Person person) {
        person = personService.findById(person);
        String email = person.getEmail();
        if (StringUtils.isEmpty(email)) {
            return;
        }
        try {
            String templateId = SendGridEmailTemplate.ISSUE_CONFIRM();
            String subject = "Resolved issue confirmation";
            Problem problem = null;
            List<Problem> problemList = problemService.findByIssue(new SearchCondition(), issue);
            if (problemList.size() > 0) problem = problemList.get(0);
            Exam exam = problem.getExam();
            exam = examService.findById(exam);
            Map<String, Object> data = new HashMap<>();
            String serviceName = exam.getName();
            String note = problem.getRemark();
            String serviceTime = getDateTimeStr(issue.getApptMinDate()) + " - " + getDateTimeStr(issue.getApptMaxDate());
            data.put("serviceName", serviceName);
            data.put("serviceTime", serviceTime);
            data.put("note", note);
            emailUtil.sendByTemplate(email, subject, templateId, data);
        } catch (Exception e) {
            LogUtil.error("send email fail" + email, e);
        }
    }

    private String getDateTimeStr(String apptMinDate){
        if(StringUtils.isEmpty(apptMinDate)){
            return "";
        }
        LocalDateTime localDateTime = LocalDateTime.parse(apptMinDate);
        int day = localDateTime.getDayOfMonth();
        String month = localDateTime.getMonth().toString();
        int year = localDateTime.getYear();

        return month+day+","+year;
    }

    @GetMapping({"/mg/issue/{id}"})
    public Rest<Issue> findById(Issue e) throws ParameterException {
        ParameterExceptionUtil.verify("issue.id", e.getId()).isPositive();
        Issue data = service.findById(e);
        data.setVehicle(vehicleService.findById(data.getVehicle()));
        data.setDriver(personService.findById(data.getDriver()));
        List<Problem> problemList = problemService.findByIssue(new SearchCondition(), data);
        if (problemList.size() > 0) data.setProblem(problemList.get(0));
        return Rest.createSuccess(data);
    }

    @GetMapping({"/mg/issue/count"})
    public Rest<Integer> count(SearchCondition searchCondition) {
    	if (searchCondition.getName() != null || searchCondition.getGroup() != null) {
    		searchCondition.setIdList(vehicleService.find(searchCondition).stream().map(e->e.getId().toString()).collect(Collectors.toList()));
    		if (searchCondition.getIdList().size() == 0) searchCondition.setIdList(Arrays.asList("0"));
    	}
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }

    @GetMapping({"/mg/issue"})
    public Rest<List<Issue>> find(SearchCondition searchCondition) {
    	if (searchCondition.getName() != null || searchCondition.getGroup() != null) {
    		searchCondition.setIdList(vehicleService.find(searchCondition).stream().map(e->e.getId().toString()).collect(Collectors.toList()));
    		if (searchCondition.getIdList().size() == 0) searchCondition.setIdList(Arrays.asList("0"));
    	}
        List<Issue> list = service.find(searchCondition);
        list.stream().forEach(e -> {
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));
            List<Problem> problemList = problemService.findByIssue(new SearchCondition(), e);
            if (problemList.size() > 0) e.setProblem(problemList.get(0));
        });
        return Rest.createSuccess(list);
    }

    @GetMapping({"/mg/vehicle/{id}/issue/count"})
    public Rest<Integer> countByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        int count = service.countByVehicle(searchCondition, vehicle);
        return Rest.createSuccess(count);
    }

    @GetMapping({"/mg/vehicle/{id}/issue"})
    public Rest<List<Issue>> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        List<Issue> list = service.findByVehicle(searchCondition, vehicle);
        list.stream().forEach(e -> {
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));
            List<Problem> problemList = problemService.findByIssue(new SearchCondition(), e);
            if (problemList.size() > 0) e.setProblem(problemList.get(0));
        });
        return Rest.createSuccess(list);
    }

    @GetMapping({"/mg/driver/{id}/issue/count"})
    public Rest<Integer> countByDriver(SearchCondition searchCondition, Person driver) throws ParameterException {
        ParameterExceptionUtil.verify("driver.id", driver.getId()).isPositive();
        int count = service.countByDriver(searchCondition, driver);
        return Rest.createSuccess(count);
    }

    @GetMapping("/mg/driver/{id}/issue")
    public Rest<List<Issue>> findByDriver(SearchCondition searchCondition, Person driver) throws ParameterException {
        ParameterExceptionUtil.verify("driver.id", driver.getId()).isPositive();
        List<Issue> list = service.findByDriver(searchCondition, driver);
        list.stream().forEach(e -> {
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));
            List<Problem> problemList = problemService.findByIssue(new SearchCondition(), e);
            if (problemList.size() > 0) e.setProblem(problemList.get(0));
        });
        return Rest.createSuccess(list);
    }

    //for testcase's code usage
    public void setPushManager(PushManager pushManager) {
        this.pushManager = pushManager;
    }
    //for testcase's code usage
    public void setEmailUtil(SendGridEmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }
}
