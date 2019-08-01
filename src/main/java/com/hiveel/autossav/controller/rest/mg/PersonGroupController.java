package com.hiveel.autossav.controller.rest.mg;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.PersonGroup;
import com.hiveel.autossav.service.PersonGroupService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonGroupController {
    @Autowired
    private PersonGroupService service;

    @PostMapping("/mg/personGroup")
    public Rest<Long> save(PersonGroup e) throws ParameterException {
    	ParameterExceptionUtil.verify("personGroup.type", e.getType()).isNotNull();
        ParameterExceptionUtil.verify("personGroup.name", e.getName()).isLengthIn(1, 50);
        service.save(e);
        return Rest.createSuccess(e.getId());
    }

    @DeleteMapping("/mg/personGroup/{id}")
    public Rest<Boolean> delete(PersonGroup e) throws ParameterException {
        ParameterExceptionUtil.verify("personGroup.id", e.getId()).isPositive();
        boolean success = service.delete(e);
        return Rest.createSuccess(success);
    }

    @PutMapping("/mg/personGroup/{id}")
    public Rest<Boolean> update(PersonGroup e) throws ParameterException {
        ParameterExceptionUtil.verify("personGroup.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("personGroup.type | name | dashboard | inspection | issues | service | vehicle | user | finacial | setting | notification",
                e.getType(), e.getName(), e.getDashboard(), e.getInspection(), e.getIssues(), e.getExam(), e.getVehicle(), e.getPerson(), e.getInvoice() ,e.getSetting(), e.getNotification()).atLeastOne().isNotEmpty();
        boolean success = service.update(e);
        return Rest.createSuccess(success);
    }

    @GetMapping({"/mg/personGroup/{id}","/dr/personGroup/{id}"})
    public Rest<PersonGroup> findById(PersonGroup e) throws ParameterException {
        ParameterExceptionUtil.verify("personGroup.id", e.getId()).isPositive();
        PersonGroup data = service.findById(e);
        return Rest.createSuccess(data);
    }

    @GetMapping({"/mg/personGroup/count","/dr/personGroup/count"})
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/mg/personGroup","/dr/personGroup"})
    public Rest<List<PersonGroup>> find(SearchCondition searchCondition) {
        List<PersonGroup> list = service.find(searchCondition);
        return Rest.createSuccess(list);
    }
}
