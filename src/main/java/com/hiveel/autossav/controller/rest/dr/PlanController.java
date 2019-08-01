package com.hiveel.autossav.controller.rest.dr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Plan;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.service.PlanService;
import com.hiveel.autossav.service.VehicleService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;


@RestController(value="DriverPlanController")
public class PlanController {
    @Autowired
    private PlanService service;
    @Autowired
    private VehicleService vehicleService;   

    @GetMapping("/dr/vehicle/{id}/plan")
    public Rest<List<Plan>> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
    	ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        List<Plan> list = service.findByVehicle(searchCondition, vehicle);
        list.stream().forEach(e->e.setVehicle(vehicleService.findById(e.getVehicle())));
        return Rest.createSuccess(list);
    }
}
