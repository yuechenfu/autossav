package com.hiveel.autossav.controller.rest.dr;

import com.hiveel.autossav.manager.PushManager;
import com.hiveel.autossav.model.ProjectRestCode;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.OdometerService;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.autossav.service.VehicleDriverRelateService;
import com.hiveel.autossav.service.VehicleService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.util.ThreadUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController(value="DriverVehicleDriverRelateController")
public class VehicleDriverRelateController {
    @Autowired
    private VehicleDriverRelateService service;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private PersonService personService;
    @Autowired
    private OdometerService odometerService;
    @Autowired
    private VehicleDriverRelateService vehicleDriverRelateService;
    @Autowired
    private PushManager pushManager;

    @PostMapping("/dr/me/vehicleDriverRelate")
    public Rest<Long> save(@RequestAttribute("loginPerson")Person loginPerson, VehicleDriverRelate e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicleDriverRelate.vehicle", e.getVehicle()).isNotNull();
        ParameterExceptionUtil.verify("vehicleDriverRelate.vehicle.id", e.getVehicle().getId()).isPositive();
        ParameterExceptionUtil.verify("vehicleDriverRelate.onDate", e.getOnDate()).isNotEmpty();
        ParameterExceptionUtil.verify("vehicleDriverRelate.onOdometer", e.getOnOdometer()).isNotEmpty();
        e.setDriver(loginPerson);
        SearchCondition relateSearchCondition = new SearchCondition();
        relateSearchCondition.setOffDate("");
        List<VehicleDriverRelate> list = vehicleDriverRelateService.findByVehicle(relateSearchCondition, e.getVehicle());
        if (list.size() == 1 && list.get(0).getDriver().getId() == e.getDriver().getId()) return Rest.createSuccess(list.get(0).getId());
        if (list.size() == 1 && list.get(0).getDriver().getId() != e.getDriver().getId()) return Rest.createFail(ProjectRestCode.HAS_BEEN_REGISTERED);
        service.save(e);
        ThreadUtil.run(()->{
            Odometer odometer = new Odometer.Builder().set("relateId", e.getId()).set("type", OdometerType.ON_ODOMETER).set("vehicle", e.getVehicle()).set("mi", Integer.valueOf(e.getOnOdometer())).set("date", e.getOnDate()).build();
            odometerService.save(odometer);
            e.getVehicle().setOdometer(Integer.valueOf(e.getOnOdometer()));
        	e.getVehicle().setStatus(VehicleStatus.ACTIVE);
        	vehicleService.update(e.getVehicle());
        });
        pushWs(e, PushRecordType.WS_VehicleDriverRelate_SIGNIN, PersonType.VE); // 司机sign in车辆  给全部 VE权限的用户推送一条信息
        return Rest.createSuccess(e.getId());
    }

    private void pushWs(VehicleDriverRelate e, PushRecordType pushType , PersonType personType) {
        ThreadUtil.run(() -> {
            VehicleDriverRelate inDb = service.findById(e);
            Person person = inDb.getDriver();
            Vehicle vehicle = vehicleService.findById(inDb.getVehicle());
            PushRecord pushRecord = new PushRecord.Builder().set("person", person).set("type", pushType).build();
            pushRecord.setRelateId(e.getId());
            pushRecord.addPayLoad("vehicle", vehicle).addPayLoad("vehicleDriverRelate", e);
            pushManager.pushAll2WebSocket(pushRecord, personType );
        });
    }

    @PutMapping({"/dr/me/vehicleDriverRelate/{id}"})
    public Rest<Boolean> update(@RequestAttribute("loginPerson")Person loginPerson, VehicleDriverRelate e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicleDriverRelate.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("vehicleDriverRelate.offDate", e.getOffDate()).isNotEmpty();
        ParameterExceptionUtil.verify("vehicleDriverRelate.offOdometer", e.getOffOdometer()).isNotEmpty();
        e.setDriver(loginPerson);
        boolean success = service.update(e);
        ThreadUtil.run(()->{
        	VehicleDriverRelate data = service.findById(e);
        	data.getVehicle().setOdometer(Integer.valueOf(e.getOffOdometer()));
        	data.getVehicle().setStatus(VehicleStatus.INACTIVE);
        	vehicleService.update(data.getVehicle());
            Odometer odometer = new Odometer.Builder().set("relateId", 0L).set("type", OdometerType.OFF_ODOMETER).set("vehicle", data.getVehicle()).set("mi", Integer.valueOf(e.getOffOdometer())).set("date", e.getOffDate()).build();
            odometerService.save(odometer); 
        });
        pushWs(e, PushRecordType.WS_VehicleDriverRelate_SIGNOUT, PersonType.VE); // 司机sign out车辆  给全部 VE权限的用户推送一条信息
        return Rest.createSuccess(success);
    }

    @GetMapping({"/dr/me/vehicleDriverRelate/{id}"})
    public Rest<VehicleDriverRelate> findById(@RequestAttribute("loginPerson")Person loginPerson, VehicleDriverRelate e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicleDriverRelate.id", e.getId()).isPositive();
        e.setDriver(loginPerson);
        VehicleDriverRelate data = service.findById(e);
        data.setVehicle(vehicleService.findById(data.getVehicle()));
        data.setDriver(personService.findById(data.getDriver()));
        return Rest.createSuccess(data);
    }

    @GetMapping({"/dr/me/vehicle/{id}/vehicleDriverRelate"})
    public Rest<List<VehicleDriverRelate>> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle", vehicle).isNotNull();
        ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();    	
        List<VehicleDriverRelate> list = service.findByVehicle(searchCondition, vehicle);
        list.stream().forEach(e->{
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));
        });
        return Rest.createSuccess(list);
    }

    @GetMapping({"/dr/me/vehicleDriverRelate"})
    public Rest<List<VehicleDriverRelate>> findByDriver(@RequestAttribute("loginPerson")Person loginPerson, SearchCondition searchCondition)  throws ParameterException {
    	searchCondition.setOffDate("");
        List<VehicleDriverRelate> list = service.findByDriver(searchCondition, loginPerson);
        list.stream().forEach(e->{
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));
        });
        return Rest.createSuccess(list);
    }
    
    @GetMapping({"/dr/me/vehicleDriverRelate/history"})
    public Rest<List<VehicleDriverRelate>> findByDriverHistory(@RequestAttribute("loginPerson")Person loginPerson, SearchCondition searchCondition)  throws ParameterException {
        List<VehicleDriverRelate> list = service.findByDriver(searchCondition, loginPerson);
        list.stream().forEach(e->{
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));
        });
        return Rest.createSuccess(list);
    }
}
