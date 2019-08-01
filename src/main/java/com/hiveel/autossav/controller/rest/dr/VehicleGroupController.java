package com.hiveel.autossav.controller.rest.dr;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.VehicleGroup;
import com.hiveel.autossav.service.VehicleGroupService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController(value="DriverVehicleGroupController")
public class VehicleGroupController {
    @Autowired
    private VehicleGroupService service;

    @GetMapping({"/dr/vehicleGroup/{id}"})
    public Rest<VehicleGroup> findById(VehicleGroup e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicleGroup.id", e.getId()).isPositive();
        VehicleGroup data = service.findById(e);
        return Rest.createSuccess(data);
    }

    @GetMapping("/dr/vehicleGroup/count")
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }
    @GetMapping("/dr/vehicleGroup")
    public Rest<List<VehicleGroup>> find(SearchCondition searchCondition) {
        List<VehicleGroup> list = service.find(searchCondition);
        return Rest.createSuccess(list);
    }
}
