package com.hiveel.autossav.controller.rest.as;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Plan;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.service.AddressService;
import com.hiveel.autossav.service.PlanService;
import com.hiveel.autossav.service.VehicleService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;

@RestController
public class PlanController {
    @Autowired
    private PlanService service;
    @Autowired
    private VehicleService vehicleService;  
    @Autowired
    private AddressService addressService;   

    @PostMapping("/mg/plan")
    public Rest<Long> save(Plan e) throws ParameterException {
        ParameterExceptionUtil.verify("plan.day", e.getDay()).isIn(1, 31);
        ParameterExceptionUtil.verify("plan.vehicle", e.getVehicle()).isNotNull();
        ParameterExceptionUtil.verify("plan.vehicle.id", e.getVehicle().getId()).isPositive();
        ParameterExceptionUtil.verify("plan.address", e.getAddress()).isNotNull();
        ParameterExceptionUtil.verify("plan.address.id", e.getAddress().getId()).isPositive();
        service.save(e);
        return Rest.createSuccess(e.getId());
    }
    @DeleteMapping("/mg/plan/{id}")
    public Rest<Boolean> delete(Plan e) throws ParameterException {
        ParameterExceptionUtil.verify("plan.id", e.getId()).isPositive();
        boolean success = service.delete(e);
        return Rest.createSuccess(success);
    }
    @PutMapping("/mg/plan/{id}")
    public Rest<Boolean> update(Plan e) throws ParameterException {
        ParameterExceptionUtil.verify("plan.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("plan.day", e.getDay()).isIn(1, 31);
        boolean success = service.update(e);
        return Rest.createSuccess(success);
    }

    @GetMapping({"/mg/plan/{id}"})
    public Rest<Plan> findById(Plan e) throws ParameterException {
        ParameterExceptionUtil.verify("plan.id", e.getId()).isPositive();
        Plan plan = service.findById(e);
        plan.setVehicle(vehicleService.findById(plan.getVehicle()));
        plan.setAddress(addressService.findById(plan.getAddress()));
        return Rest.createSuccess(plan);
    }
    @GetMapping({"/mg/plan/count"})
    public Rest<Integer> count(SearchCondition searchCondition){
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }

    @GetMapping({"/mg/plan"})
    public Rest<List<Plan>> find(SearchCondition searchCondition){
        List<Plan> list = service.find(searchCondition);
        list.stream().forEach(e->{
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setAddress(addressService.findById(e.getAddress()));
        });
        return Rest.createSuccess(list);
    }
    @GetMapping("/mg/vehicle/{id}/plan")
    public Rest<List<Plan>> findByVehicle(SearchCondition searchCondition, Vehicle vehicle){
        List<Plan> list = service.findByVehicle(searchCondition, vehicle);
        list.stream().forEach(e->{
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setAddress(addressService.findById(e.getAddress()));
        });
        return Rest.createSuccess(list);
    }

    @GetMapping({"/mg/plan/no/vehicle/count"})
    public Rest<Integer> countVehicleWithoutPlan(SearchCondition searchCondition){
        List<Plan> list = service.find(searchCondition);
        List<Vehicle> vehicleList = list.stream().map(e->e.getVehicle()).collect(Collectors.toList());
        SearchCondition vehicleSearchCondition = new SearchCondition();
        int count = vehicleService.countExceptVehicleList(vehicleSearchCondition, vehicleList);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/mg/plan/no/vehicle"})
    public Rest<List<Vehicle>> findVehicleWithoutPlan(SearchCondition searchCondition){
        List<Plan> list = service.find(searchCondition);
        List<Vehicle> vehicleList = list.stream().map(e->e.getVehicle()).collect(Collectors.toList());
        SearchCondition vehicleSearchCondition = new SearchCondition();
        List<Vehicle> result = vehicleService.findExceptVehicleList(vehicleSearchCondition, vehicleList);
        return Rest.createSuccess(result);
    }
}
