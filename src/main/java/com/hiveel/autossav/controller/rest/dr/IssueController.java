package com.hiveel.autossav.controller.rest.dr;

import java.util.List;
import java.util.stream.Collectors;

import com.hiveel.autossav.manager.PushManager;
import com.hiveel.autossav.model.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.service.IssueService;
import com.hiveel.autossav.service.OdometerService;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.autossav.service.ProblemService;
import com.hiveel.autossav.service.VehicleService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.util.ThreadUtil;

@RestController(value="DriverIssueController")
public class IssueController {
    @Autowired
    private IssueService service;
    @Autowired
    private PersonService personService;  
    @Autowired
    private VehicleService vehicleService;    
    @Autowired
    private ProblemService problemService; 
    @Autowired
    private OdometerService odometerService;
    @Autowired
    private PushManager pushManager;

    @PostMapping({"/dr/me/issue"})
    public Rest<Long> save(@RequestAttribute("loginPerson") Person driver, Issue e) throws ParameterException {
        ParameterExceptionUtil.verify("issue.vehicle", e.getVehicle()).isNotNull();
        ParameterExceptionUtil.verify("issue.vehicle.id", e.getVehicle().getId()).isPositive();
        ParameterExceptionUtil.verify("issue.odometer", e.getOdometer()).isPositive();
        e.setDriver(driver);
        service.save(e);
        ThreadUtil.run(() -> {
            Odometer odometer = new Odometer.Builder().set("relateId", e.getId()).set("type", OdometerType.ISSUE).set("vehicle", e.getVehicle()).set("mi", e.getOdometer()).set("date", e.getApptMinDate()).build();
            odometerService.save(odometer);
            Vehicle vehicle = e.getVehicle();
            vehicle.setOdometer(e.getOdometer());
            vehicleService.update(vehicle);
        });
        pushWs(e, PushRecordType.WS_ISSUE_CREATE);
        return Rest.createSuccess(e.getId());
    }

    private void pushWs(Issue e, PushRecordType pushType) {
        ThreadUtil.run(() -> {
            Issue inDb = service.findById(e);
            Person person = inDb.getDriver();
            Vehicle vehicle = vehicleService.findById(inDb.getVehicle());
            PushRecord pushRecord = new PushRecord.Builder().set("person", person).set("type", pushType).build();
            pushRecord.setRelateId(e.getId());
            pushRecord.addPayLoad("vehicle", vehicle).addPayLoad("issue", e);
            pushManager.pushAll2WebSocket(pushRecord, PersonType.AS);
            pushManager.pushAll2WebSocket(pushRecord, PersonType.VE);
        });
    }

    @PutMapping("/dr/issue/{id}/status/scheduled")
    public Rest<Boolean> updateStatus(Issue e) throws ParameterException {
        ParameterExceptionUtil.verify("issue.id", e.getId()).isPositive();
        boolean success = service.update(new Issue.Builder().set("id", e.getId()).set("status", IssueStatus.SCHEDULED).build());
        pushWs(e, PushRecordType.WS_ISSUE_SCHEDULED);
        return Rest.createSuccess(success);
    }
    @GetMapping({"/dr/issue/{id}"})
    public Rest<Issue> findById(Issue e) throws ParameterException {
        ParameterExceptionUtil.verify("issue.id", e.getId()).isPositive();
        Issue data = service.findById(e);
        data.setVehicle(vehicleService.findById(data.getVehicle()));
        data.setDriver(personService.findById(data.getDriver()));
        List<Problem> problemList=problemService.findByIssue(new SearchCondition(), data);
        if (problemList.size() > 0) data.setProblem(problemList.get(0));     
        return Rest.createSuccess(data);
    }
    @GetMapping({"/dr/me/issue/count"})
    public Rest<Integer> count(@RequestAttribute("loginPerson") Person driver, SearchCondition searchCondition){
        int count = service.countByDriver(searchCondition, driver);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/dr/me/issue"})
    public Rest<List<Issue>> find(@RequestAttribute("loginPerson") Person driver, SearchCondition searchCondition){
        List<Issue> list = service.findByDriver(searchCondition, driver);
        list.stream().forEach(e->{
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));   
            List<Problem> problemList=problemService.findByIssue(new SearchCondition(), e);
            if (problemList.size() > 0) e.setProblem(problemList.get(0));   
        });
        return Rest.createSuccess(list);
    }
    @GetMapping({"/dr/vehicle/{id}/issue/count"})
    public Rest<Integer> countByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
    	ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        int count = service.countByVehicle(searchCondition,vehicle);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/dr/vehicle/{id}/issue"})
    public Rest<List<Issue>> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        List<Issue> list = service.findByVehicle(searchCondition, vehicle);
        list.stream().forEach(e->{
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));      
            List<Problem> problemList=problemService.findByIssue(new SearchCondition(), e);
            if (problemList.size() > 0) e.setProblem(problemList.get(0));   
        });
        return Rest.createSuccess(list);
    }
}
