package com.hiveel.autossav.controller.rest.mg;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.VehicleDriverRelate;
import com.hiveel.autossav.model.entity.VehicleStatus;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.Odometer;
import com.hiveel.autossav.model.entity.OdometerType;
import com.hiveel.autossav.model.entity.Person;
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

@RestController
public class VehicleDriverRelateController {
    @Autowired
    private VehicleDriverRelateService service;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private PersonService personService;
    @Autowired
    private OdometerService odometerService;

    @PutMapping({"/mg/vehicleDriverRelate/{id}"})
    public Rest<Boolean> update(VehicleDriverRelate e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicleDriverRelate.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("vehicleDriverRelate.offDate", e.getOffDate()).isNotEmpty();
        ParameterExceptionUtil.verify("vehicleDriverRelate.offOdometer", e.getOffOdometer()).isNotEmpty();
        boolean success = service.update(e);
        ThreadUtil.run(() -> {
        	VehicleDriverRelate data = service.findById(e);
        	data.getVehicle().setOdometer(Integer.valueOf(e.getOffOdometer()));
        	data.getVehicle().setStatus(VehicleStatus.INACTIVE);
        	vehicleService.update(data.getVehicle());
            Odometer odometer = new Odometer.Builder().set("relateId", 0L).set("type", OdometerType.OFF_ODOMETER).set("vehicle", data.getVehicle()).set("mi", Integer.valueOf(e.getOffOdometer())).set("date", e.getOffDate()).build();
            odometerService.save(odometer); 
        });
        return Rest.createSuccess(success);
    }

    @GetMapping({"/mg/vehicleDriverRelate/{id}"})
    public Rest<VehicleDriverRelate> findById(VehicleDriverRelate e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicleDriverRelate.id", e.getId()).isPositive();
        VehicleDriverRelate data = service.findById(e);
        data.setVehicle(vehicleService.findById(data.getVehicle()));
        data.setDriver(personService.findById(data.getDriver()));
        return Rest.createSuccess(data);
    }

    @GetMapping({"/mg/vehicleDriverRelate/count"})
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/mg/vehicleDriverRelate"})
    public Rest<List<VehicleDriverRelate>> find(SearchCondition searchCondition) {
        List<VehicleDriverRelate> list = service.find(searchCondition);
        list.stream().forEach(e->{
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));
        });
        return Rest.createSuccess(list);
    }

    @GetMapping({"/mg/vehicle/{id}/vehicleDriverRelate/count"})
    public Rest<Integer> countByVehicle(SearchCondition searchCondition, Vehicle vehicle) {
        int count = service.countByVehicle(searchCondition, vehicle);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/mg/vehicle/{id}/vehicleDriverRelate"})
    public Rest<List<VehicleDriverRelate>> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
    	ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        List<VehicleDriverRelate> list = service.findByVehicle(searchCondition, vehicle);
        list.stream().forEach(e->{
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));
        });
        return Rest.createSuccess(list);
    }

    @GetMapping({"/mg/driver/{id}/vehicleDriverRelate/count"})
    public Rest<Integer> countByDriver(SearchCondition searchCondition, Person driver) {
        int count = service.countByDriver(searchCondition, driver);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/mg/driver/{id}/vehicleDriverRelate"})
    public Rest<List<VehicleDriverRelate>> findByDriver(SearchCondition searchCondition, Person driver) throws ParameterException {
    	ParameterExceptionUtil.verify("driver.id", driver.getId()).isPositive();
        List<VehicleDriverRelate> list = service.findByDriver(searchCondition, driver);
        list.stream().forEach(e->{
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));
        });
        return Rest.createSuccess(list);
    }
}
