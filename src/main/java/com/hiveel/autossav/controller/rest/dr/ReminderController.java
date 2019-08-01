package com.hiveel.autossav.controller.rest.dr;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.Reminder;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.service.InspectionService;
import com.hiveel.autossav.service.ReminderService;
import com.hiveel.autossav.service.VehicleService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController(value="DriverReminderController")
public class ReminderController {
    @Autowired
    private ReminderService service;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private InspectionService inspectionService;

    @GetMapping({"/dr/me/reminder/{id}"})
    public Rest<Reminder> findById(Reminder e) throws ParameterException {
        ParameterExceptionUtil.verify("reminder.id", e.getId()).isPositive();
        Reminder data = service.findById(e);
        data.setVehicle(vehicleService.findById(data.getVehicle()));
        data.setInspection(inspectionService.findById(data.getInspection()));
        return Rest.createSuccess(data);
    }

    @GetMapping({"/dr/me/reminder/count"})
    public Rest<Integer> count(@RequestAttribute("loginPerson")Person loginPerson, SearchCondition searchCondition) {
        int count = service.countByDriver(searchCondition, loginPerson);
        return Rest.createSuccess(count);
    }
    
    @GetMapping({"/dr/me/reminder"})
    public Rest<List<Reminder>> findByDriver(@RequestAttribute("loginPerson")Person loginPerson, SearchCondition searchCondition) throws ParameterException {
        List<Reminder> list = service.findByDriver(searchCondition, loginPerson);
        list.stream().forEach(e->{
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setInspection(inspectionService.findById(e.getInspection()));
        });
        return Rest.createSuccess(list);
    }

    @GetMapping({"/dr/me/vehicle/{id}/reminder"})
    public Rest<List<Reminder>> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
    	ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        List<Reminder> list = service.findByVehicle(searchCondition, vehicle);
        list.stream().forEach(e->{
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setInspection(inspectionService.findById(e.getInspection()));
        });
        return Rest.createSuccess(list);
    }
}
