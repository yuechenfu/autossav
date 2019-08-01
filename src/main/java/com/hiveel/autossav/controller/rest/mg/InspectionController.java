package com.hiveel.autossav.controller.rest.mg;

import com.hiveel.autossav.manager.PushManager;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.conf.SendGridEmailTemplate;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.InspectionService;
import com.hiveel.autossav.service.OdometerService;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.autossav.service.VehicleDriverRelateService;
import com.hiveel.autossav.service.VehicleGroupService;
import com.hiveel.autossav.service.VehicleService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.log.util.LogUtil;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.util.SendGridEmailUtil;
import com.hiveel.core.util.ThreadUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class InspectionController {
    @Autowired
    private InspectionService service;
    @Autowired
    private PersonService personService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleGroupService vehicleGroupService;
    @Autowired
    private OdometerService odometerService;
    @Autowired
    private VehicleDriverRelateService vehicleDriverRelateService;
    @Autowired
    private PushManager pushManager;
    @Autowired
    private SendGridEmailUtil emailUtil;

    @PostMapping("/mg/inspection")
    public Rest<Long> save(Inspection e) throws ParameterException {
        ParameterExceptionUtil.verify("inspection.vehicle", e.getVehicle()).isNotNull();
        ParameterExceptionUtil.verify("inspection.vehicle.id", e.getVehicle().getId()).isPositive();
        ParameterExceptionUtil.verify("inspection.driver", e.getDriver()).isNotNull();
        ParameterExceptionUtil.verify("inspection.driver.id", e.getDriver().getId()).isPositive();
        ParameterExceptionUtil.verify("inspection.autosave", e.getDriver()).isNotNull();
        ParameterExceptionUtil.verify("inspection.autosave.id", e.getDriver().getId()).isPositive();
        ParameterExceptionUtil.verify("inspection.name", e.getName()).isNotEmpty();
        ParameterExceptionUtil.verify("inspection.date", e.getDate()).isDateTime();
        ParameterExceptionUtil.verify("inspection.odometer", e.getOdometer()).isPositive();        
        service.save(e);
        ThreadUtil.run(()->{
            Odometer odometer = new Odometer.Builder().set("relateId", e.getId()).set("type", OdometerType.INSPECTION).set("vehicle", e.getVehicle()).set("mi", e.getOdometer()).set("date", e.getDate()).build();
        	odometerService.save(odometer);
        	Vehicle vehicle = e.getVehicle();
        	vehicle.setOdometer(e.getOdometer());
        	vehicleService.update(vehicle);
        });
        return Rest.createSuccess(e.getId());
    }

    @DeleteMapping({"/mg/inspection/{id}"})
    public Rest<Boolean> delete(Inspection e) throws ParameterException {
        ParameterExceptionUtil.verify("inspection.id", e.getId()).isPositive();
        boolean success = service.delete(e);
        return Rest.createSuccess(success);
    }

    @PutMapping("/mg/inspection/{id}")
    public Rest<Boolean> update(Inspection e) throws ParameterException {
        ParameterExceptionUtil.verify("inspection.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("inspection.name | date | odometer | content | status | tax", e.getName(), e.getDate(), e.getOdometer(), e.getContent(), e.getStatus(), e.getTax()).atLeastOne().isNotEmpty();
        boolean success = service.update(e);
        if (success && e.getStatus() == InspectionStatus.QUOTED) updateVehicleStatusAsProblem(e);
        if (success && e.getStatus() == InspectionStatus.COMPLETE) {
            push(e);
            updateVehicleStatusByRelate(e);
        }
        if (e.getOdometer() != null || e.getDate() != null) updateVehicleOdometer(e);
        return Rest.createSuccess(success);
    }

    @PutMapping("/mg/inspection/{id}/status")
    public Rest<Boolean> updateStatus(@RequestAttribute("loginPerson") Person loginPerson, Inspection e) throws ParameterException {
        ParameterExceptionUtil.verify("inspection.id", e.getId()).isPositive();
        if (loginPerson.getType() == PersonType.VE) {
            e.setStatus(InspectionStatus.CONFIRM);
        } else if (loginPerson.getType() == PersonType.AS) {
            ParameterExceptionUtil.verify("status", e.getStatus().toString()).beContainedIn(Arrays.asList("QUOTED", "COMPLETE"));
        }
        boolean success = service.update(e);
        if (success && e.getStatus() == InspectionStatus.QUOTED) {
            updateVehicleStatusAsProblem(e);
            pushWs(e, PushRecordType.WS_INSPCTION_QUOTE, PersonType.VE); // 有新的inspection报价 给全部 VE权限的用户推送一条信息
        }
        if (success && e.getStatus() == InspectionStatus.COMPLETE) {
            push(e);
            pushWs(e, PushRecordType.WS_INSPECTION_COMPLETE, PersonType.VE);  //inspection事件已经完成 给全部 VE权限的用户推送一条信息
            updateVehicleStatusByRelate(e);
        }
        if (success && e.getStatus() == InspectionStatus.CONFIRM) {
            pushWs(e, PushRecordType.WS_INSPECTION_CONFIRM, PersonType.AS);  //车队已经通过inspection报价 给全部 AS权限的用户推送一条信息
        }
        return Rest.createSuccess(success);
    }
    
    private void updateVehicleOdometer(Inspection e) {
        ThreadUtil.run(()->{
        	Odometer data = odometerService.findByInspection(new SearchCondition(), e);
    		data.setMi(e.getOdometer());
    		data.setDate(e.getDate());
    		odometerService.update(data);     
        	Vehicle vehicle = data.getVehicle();
        	vehicle.setOdometer(e.getOdometer());
        	vehicleService.update(vehicle);
        });
    }
    
    private void updateVehicleStatusAsProblem(Inspection e) {
    	ThreadUtil.run(()->{
            Vehicle vehicle = service.findById(e).getVehicle();
            vehicle.setStatus(VehicleStatus.PROBLEM);
            vehicleService.update(vehicle);
    	});
    }
    
    private void updateVehicleStatusByRelate(Inspection e) {
    	ThreadUtil.run(()->{
            Inspection data = service.findById(e);
            SearchCondition searchCondition=new SearchCondition();
            searchCondition.setOffDate("");
            List<VehicleDriverRelate> list = vehicleDriverRelateService.findByDriverAndVehicle(searchCondition, data.getDriver(), data.getVehicle());
            Vehicle vehicle = data.getVehicle();
            if (list.size() == 1) vehicle.setStatus(VehicleStatus.ACTIVE);
            else vehicle.setStatus(VehicleStatus.INACTIVE);
            vehicleService.update(vehicle);
    	});
    }

    private void push(Inspection e) {
        ThreadUtil.run(() -> {
            Inspection inDb = service.findById(e);
            Person person = personService.findById(inDb.getDriver());
            Vehicle vehicle = vehicleService.findById(inDb.getVehicle());
            PushRecordType type = PushRecordType.INSPECTION_COMPLETE;
            PushRecord pushRecord = new PushRecord.Builder().set("person", person).set("type", type).build();
            pushRecord.setRelateId(e.getId());
            pushRecord.addPayLoad("vehicle", vehicle).addPayLoad("inspection", e);
            pushManager.pushAndSaveRecord(pushRecord);
            sendMail(vehicle,person);
        });
    }

    private void sendMail(Vehicle vehicle, Person person) {
        String email = person.getEmail();
        if (StringUtils.isEmpty(email)) {
            return;
        }
        String templateId = SendGridEmailTemplate.INSPECTION_COMPLETE();
        String carName = vehicle.getName();
        String subject = "Inspection has been completed!";
        Map<String, Object> data = new HashMap<>();
        data.put("carName", carName);
        try {
            emailUtil.sendByTemplate(email, subject, templateId, data);
        } catch (Exception e) {
            LogUtil.error("send email fail" + email, e);
        }
    }

    private void pushWs(Inspection e, PushRecordType pushType, PersonType personType) {
        ThreadUtil.run(() -> {
            Inspection inDb = service.findById(e);
            Person person = inDb.getDriver();
            Vehicle vehicle = vehicleService.findById(inDb.getVehicle());
            PushRecord pushRecord = new PushRecord.Builder().set("person", person).set("type", pushType).build();
            pushRecord.setRelateId(e.getId());
            pushRecord.addPayLoad("vehicle", vehicle).addPayLoad("inspection", e);
            pushManager.pushAll2WebSocket(pushRecord, personType);
        });
    }


    @GetMapping({"/mg/inspection/{id}"})
    public Rest<Inspection> findById(Inspection e) throws ParameterException {
        ParameterExceptionUtil.verify("inspection.id", e.getId()).isPositive();
        Inspection data = service.findById(e);
        data.setVehicle(vehicleService.findById(data.getVehicle()));
        data.setDriver(personService.findById(data.getDriver()));
        if (!data.getAutosave().isNull()) data.setAutosave(personService.findById(data.getAutosave()));
        return Rest.createSuccess(data);
    }

    @GetMapping("/mg/inspection/count")
    public Rest<Integer> count(SearchCondition searchCondition) { 
    	if (searchCondition.getName() != null || searchCondition.getGroup() != null) {
    		searchCondition.setIdList(vehicleService.find(searchCondition).stream().map(e->e.getId().toString()).collect(Collectors.toList()));
    		if (searchCondition.getIdList().size() == 0) searchCondition.setIdList(Arrays.asList("0"));
    	}
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }

    @GetMapping({"/mg/inspection"})
    public Rest<List<Inspection>> find(SearchCondition searchCondition) {
    	if (searchCondition.getName() != null || searchCondition.getGroup() != null) {
    		searchCondition.setIdList(vehicleService.find(searchCondition).stream().map(e->e.getId().toString()).collect(Collectors.toList()));
    		if (searchCondition.getIdList().size() == 0) searchCondition.setIdList(Arrays.asList("0"));
    	}
        List<Inspection> list = service.find(searchCondition);
        list.stream().forEach(e -> {
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));
            if (e.getAutosave().getId() != 0) e.setAutosave(personService.findById(e.getAutosave()));
        });
        return Rest.createSuccess(list);
    }

    @GetMapping("/mg/vehicle/{id}/inspection/count")
    public Rest<Integer> countByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        int count = service.countByVehicle(searchCondition, vehicle);
        return Rest.createSuccess(count);
    }

    @GetMapping("/mg/vehicle/{id}/inspection")
    public Rest<List<Inspection>> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        List<Inspection> list = service.findByVehicle(searchCondition, vehicle);
        list.stream().forEach(e -> {
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));
            if (e.getAutosave().getId() != 0) e.setAutosave(personService.findById(e.getAutosave()));
        });
        return Rest.createSuccess(list);
    }

    @GetMapping("/mg/driver/{id}/inspection/count")
    public Rest<Integer> countByDriver(SearchCondition searchCondition, Person driver) throws ParameterException {
        ParameterExceptionUtil.verify("driver.id", driver.getId()).isPositive();
        int count = service.countByDriver(searchCondition, driver);
        return Rest.createSuccess(count);
    }

    @GetMapping({"/mg/driver/{id}/inspection"})
    public Rest<List<Inspection>> findByDriver(SearchCondition searchCondition, Person driver) throws ParameterException {
        ParameterExceptionUtil.verify("driver.id", driver.getId()).isPositive();
        List<Inspection> list = service.findByDriver(searchCondition, driver);
        list.stream().forEach(e -> {
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));
            if (e.getAutosave().getId() != 0) e.setAutosave(personService.findById(e.getAutosave()));
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
