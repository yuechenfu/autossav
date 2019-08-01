package com.hiveel.autossav.controller.rest.mg;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.VehicleGroup;
import com.hiveel.autossav.service.VehicleGroupService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VehicleGroupController {
    @Autowired
    private VehicleGroupService service;

    @PostMapping("/mg/vehicleGroup")
    public Rest<Long> save(VehicleGroup e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicleGroup.name", e.getName()).isLengthIn(1, 50);
        if (e.getContent() != null) ParameterExceptionUtil.verify("vehicleGroup.content", e.getContent()).isLengthIn(0, 200);
        service.save(e);
        return Rest.createSuccess(e.getId());
    }

    @DeleteMapping("/mg/vehicleGroup/{id}")
    public Rest<Boolean> delete(VehicleGroup e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicleGroup.id", e.getId()).isPositive();
        boolean success = service.delete(e);
        return Rest.createSuccess(success);
    }

    @PutMapping("/mg/vehicleGroup/{id}")
    public Rest<Boolean> update(VehicleGroup e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicleGroup.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("vehicleGroup.name | content", e.getName(), e.getContent()).atLeastOne().isNotEmpty();
        boolean success = service.update(e);
        return Rest.createSuccess(success);
    }

    @GetMapping({"/mg/vehicleGroup/{id}"})
    public Rest<VehicleGroup> findById(VehicleGroup e) throws ParameterException {
        ParameterExceptionUtil.verify("vehicleGroup.id", e.getId()).isPositive();
        VehicleGroup data = service.findById(e);
        return Rest.createSuccess(data);
    }

    @GetMapping("/mg/vehicleGroup/count")
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }
    @GetMapping("/mg/vehicleGroup")
    public Rest<List<VehicleGroup>> find(SearchCondition searchCondition) {
        List<VehicleGroup> list = service.find(searchCondition);
        return Rest.createSuccess(list);
    }
}
