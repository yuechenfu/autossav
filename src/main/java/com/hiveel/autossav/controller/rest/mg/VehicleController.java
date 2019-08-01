package com.hiveel.autossav.controller.rest.mg;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.autossav.service.VehicleDriverRelateService;
import com.hiveel.autossav.service.VehicleGroupService;
import com.hiveel.autossav.service.VehicleService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.util.ThreadUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
public class VehicleController {
    @Autowired
    private VehicleService service;
    @Autowired
    private VehicleGroupService vehicleGroupService;
    @Autowired
    private VehicleDriverRelateService vehicleDriverRelateService;
    @Autowired
    private PersonService personService;

    @PostMapping("/mg/vehicle")
    public Rest<Long> save(Vehicle e) throws ParameterException {
    	ParameterExceptionUtil.verify("vehicle.group", e.getGroup()).isNotNull();
    	ParameterExceptionUtil.verify("vehicle.group.id", e.getGroup().getId()).isPositive();
        ParameterExceptionUtil.verify("vehicle.name", e.getName()).isLengthIn(1, 50);
        if (e.getVin() != null) ParameterExceptionUtil.verify("vehicle.vin", e.getVin()).isLength(17);
        e.setType(VehicleType.VE);
        service.save(e);
        return Rest.createSuccess(e.getId());
    }

    @DeleteMapping("/mg/vehicle/{id}")
    public Rest<Boolean> delete(Vehicle e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", e.getId()).isPositive();
        boolean success = service.delete(e);
        return Rest.createSuccess(success);
    }

    @PutMapping("/mg/vehicle/{id}")
    public Rest<Boolean> update(Vehicle e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("vehicle.name | group | status | vin | plate | rental", e.getName(), e.getGroup(), e.getStatus(), e.getVin(), e.getPlate(), e.getRental()).atLeastOne().isNotEmpty();
        if (e.getVin() != null) ParameterExceptionUtil.verify("vehicle.vin", e.getVin()).isLength(17);
        boolean success = service.update(e);
        if (e.getStatus() != null && e.getStatus() == VehicleStatus.INACTIVE) updateVehicleDriverRelate(e);
        return Rest.createSuccess(success);
    }

    private void updateVehicleDriverRelate(Vehicle e) {
        ThreadUtil.run(()->{
            SearchCondition relateSearchCondition = new SearchCondition();
            relateSearchCondition.setOffDate("");
            List<VehicleDriverRelate> list = vehicleDriverRelateService.findByVehicle(relateSearchCondition, e);
            list.stream().forEach(vehicleDriverRelate ->{
            	vehicleDriverRelate.setOffDate(LocalDateTime.now(ZoneId.of("UTC")).toString());
                vehicleDriverRelateService.update(vehicleDriverRelate);
            });
        });
    }

    @GetMapping({"/mg/vehicle/{id}"})
    public Rest<Vehicle> findById(Vehicle e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", e.getId()).isPositive();
        Vehicle data = service.findById(e);
        data.setGroup(vehicleGroupService.findById(data.getGroup()));
        return Rest.createSuccess(data);
    }

    @GetMapping({"/mg/vehicle/count"})
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/mg/vehicle"})
    public Rest<List<Vehicle>> find(SearchCondition searchCondition) {
        List<Vehicle> list = service.find(searchCondition);
        list.stream().parallel().forEach(e -> {
            e.setGroup(vehicleGroupService.findById(e.getGroup()));
            if (e.getStatus() == VehicleStatus.INACTIVE) {
                e.setDriver(Person.NULL);
                return;
            }
            SearchCondition relateSearchCondition = new SearchCondition();
            relateSearchCondition.setOffDate("");
            List<VehicleDriverRelate> data = vehicleDriverRelateService.findByVehicle(relateSearchCondition, e);
            e.setDriver(data.isEmpty() ? Person.NULL : personService.findById(data.get(0).getDriver()));
        });
        return Rest.createSuccess(list);
    }
    @GetMapping({"/mg/vehicleGroup/{id}/vehicle"})
    public Rest<List<Vehicle>> findByGroup(SearchCondition searchCondition, VehicleGroup group) throws ParameterException {
    	ParameterExceptionUtil.verify("group.id", group.getId()).isPositive();
        List<Vehicle> list = service.findByGroup(searchCondition, group);
        list.stream().forEach(e->e.setGroup(vehicleGroupService.findById(e.getGroup())));
        return Rest.createSuccess(list);
    }
}
