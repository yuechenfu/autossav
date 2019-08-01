package com.hiveel.autossav.controller.rest.mg;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.InspectionService;
import com.hiveel.autossav.service.IssueService;
import com.hiveel.autossav.service.OdometerService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OdometerController {
    @Autowired
    private OdometerService service;
    @Autowired
    private InspectionService inspectionService;
    @Autowired
    private IssueService issueService;

    @GetMapping({"/mg/vehicle/{id}/odometer/count"})
    public Rest<Integer> countByVehicle(SearchCondition searchCondition, Vehicle vehicle) {
        int count = service.countByVehicle(searchCondition, vehicle);
        return Rest.createSuccess(count);
    }

    @GetMapping({"/mg/vehicle/{id}/odometer"})
    public Rest<List<Odometer>> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        List<Odometer> result =  service.findByVehicle(searchCondition,vehicle);
        return Rest.createSuccess(result);
    }


}

