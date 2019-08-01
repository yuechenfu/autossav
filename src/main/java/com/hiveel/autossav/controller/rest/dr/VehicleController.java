package com.hiveel.autossav.controller.rest.dr;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.OdometerService;
import com.hiveel.autossav.service.VehicleDriverRelateService;
import com.hiveel.autossav.service.VehicleGroupService;
import com.hiveel.autossav.service.VehicleService;
import com.hiveel.core.debug.DebugSetting;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.UnauthorizationException;
import com.hiveel.core.exception.type.RestCodeException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.log.util.LogUtil;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.model.rest.RestCode;
import com.hiveel.core.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@RestController(value="DriverVehicleController")
public class VehicleController {
    @Autowired
    private VehicleService service;
    @Autowired
    private VehicleDriverRelateService vehicleDriverRelateService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleGroupService vehicleGroupService;
    @Autowired
    private OdometerService odometerService;

    @PostMapping("/dr/vehicle")
    public Rest<Long> saveByDriver(@RequestAttribute("loginPerson")Person driver,Vehicle e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.vin", e.getVin()).isLengthIn(1, 50);
        ParameterExceptionUtil.verify("vehicle.odometer", e.getOdometer()).isPositive();
        e.setType(VehicleType.DR_PRIVATE);
        service.save(e);
        saveVehicleDriverRelate(e,driver);
        return Rest.createSuccess(e.getId());
    }

    private void saveVehicleDriverRelate(Vehicle e, Person driver) {
        ThreadUtil.run(()->{
            String onDate = LocalDateTime.now(ZoneId.of("UTC")).toString();
            VehicleDriverRelate vehicleDriverRelate = new VehicleDriverRelate.Builder()
                    .set("driver", driver)
                    .set("onDate", onDate)
                    .set("vehicle", e)
                    .set("onOdometer", e.getOdometer().toString()).build();
            vehicleDriverRelateService.save(vehicleDriverRelate);
            Odometer odometer = new Odometer.Builder().set("relateId", vehicleDriverRelate.getId()).set("type", OdometerType.ON_ODOMETER).set("vehicle", e).set("mi", Integer.valueOf(e.getOdometer())).set("date", onDate).build();
            odometerService.save(odometer);
            e.setStatus(VehicleStatus.ACTIVE);
            vehicleService.update(e);
        });
    }

    @GetMapping({"/dr/vehicle/{id}"})
    public Rest<Vehicle> findById(Vehicle e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", e.getId()).isPositive();
        Vehicle data = service.findById(e);
        data.setGroup(vehicleGroupService.findById(data.getGroup()));
        return Rest.createSuccess(data);
    }

    @GetMapping({"/dr/vehicle"})
    public Rest<List<Vehicle>> find(SearchCondition searchCondition) {
        List<Vehicle> list = service.find(searchCondition);
        list.stream().forEach(e->e.setGroup(vehicleGroupService.findById(e.getGroup())));
        return Rest.createSuccess(list);
    }

    @GetMapping({"/dr/me/vehicle"})
    public Rest<List<Vehicle>> findByMe(@RequestAttribute("loginPerson")Person loginPerson, SearchCondition searchCondition) {
    	searchCondition.setOffDate("");
    	List<VehicleDriverRelate> vehicleDriverRelateList = vehicleDriverRelateService.findByDriver(searchCondition, loginPerson);
    	List<Long> vehicleIdList = vehicleDriverRelateList.stream().map(e->e.getVehicle().getId()).distinct().collect(Collectors.toList());
    	List<Vehicle> list = vehicleIdList.stream().map(e->vehicleService.findById(new Vehicle.Builder().set("id", e).build())).collect(Collectors.toList());
        list.stream().forEach(e->e.setGroup(vehicleGroupService.findById(e.getGroup())));
        return Rest.createSuccess(list);
    }

    @GetMapping({"/dr/me/vehicle/history"})
    public Rest<List<Vehicle>> findByMeHistory(@RequestAttribute("loginPerson")Person loginPerson, SearchCondition searchCondition) {
    	List<VehicleDriverRelate> vehicleDriverRelateList = vehicleDriverRelateService.findByDriver(searchCondition, loginPerson);
    	List<Long> vehicleIdList = vehicleDriverRelateList.stream().map(e->e.getVehicle().getId()).distinct().collect(Collectors.toList());
    	List<Vehicle> list = vehicleIdList.stream().map(e->vehicleService.findById(new Vehicle.Builder().set("id", e).build())).collect(Collectors.toList());
        list.stream().forEach(e->e.setGroup(vehicleGroupService.findById(e.getGroup())));
        return Rest.createSuccess(list);
    }

    @GetMapping({"/dr/vehicleGroup/{id}/vehicle"})
    public Rest<List<Vehicle>> findByGroup(SearchCondition searchCondition, VehicleGroup group) throws ParameterException {
    	ParameterExceptionUtil.verify("group.id", group.getId()).isPositive();
        List<Vehicle> list = service.findByGroup(searchCondition, group);
        list.stream().forEach(e->e.setGroup(vehicleGroupService.findById(e.getGroup())));
        return Rest.createSuccess(list);
    }
}
