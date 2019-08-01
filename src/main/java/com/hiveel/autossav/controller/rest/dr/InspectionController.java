package com.hiveel.autossav.controller.rest.dr;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Inspection;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.service.InspectionService;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.autossav.service.VehicleService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController(value="DriverInspectionController")
public class InspectionController {
    @Autowired
    private InspectionService service;
    @Autowired
    private PersonService personService;  
    @Autowired
    private VehicleService vehicleService;   
    
    @GetMapping({"/dr/inspection/{id}"})
    public Rest<Inspection> findById(Inspection e) throws ParameterException {
        ParameterExceptionUtil.verify("inspection.id", e.getId()).isPositive();
        Inspection data = service.findById(e);
        data.setVehicle(vehicleService.findById(data.getVehicle()));
        data.setDriver(personService.findById(data.getDriver()));
        data.setAutosave(personService.findById(data.getAutosave()));
        return Rest.createSuccess(data);
    }

    @GetMapping("/dr/me/inspection/count")
    public Rest<Integer> count(@RequestAttribute("loginPerson") Person driver,SearchCondition searchCondition) {
        int count = service.countByDriver(searchCondition, driver);
        return Rest.createSuccess(count);
    }

    @GetMapping({"/dr/vehicle/{id}/inspection/count"})
    public Rest<Integer> countByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
    	ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
    	int count = service.countByVehicle(searchCondition, vehicle);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/dr/vehicle/{id}/inspection"})
    public Rest<List<Inspection>> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
    	ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        List<Inspection> list = service.findByVehicle(searchCondition, vehicle);
        list.stream().forEach(e->{
            e.setVehicle(vehicleService.findById(e.getVehicle()));
            e.setDriver(personService.findById(e.getDriver()));
            e.setAutosave(personService.findById(e.getAutosave()));        	
        });
        return Rest.createSuccess(list);
    }
}
