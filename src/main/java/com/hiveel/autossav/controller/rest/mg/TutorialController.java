package com.hiveel.autossav.controller.rest.mg;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Tutorial;
import com.hiveel.autossav.service.TutorialService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TutorialController {
    @Autowired
    private TutorialService service;

    @PostMapping("/mg/tutorial")
    public Rest<Long> save(Tutorial e) throws ParameterException {
        ParameterExceptionUtil.verify("tutorial.name", e.getName()).isLengthIn(1, 50);
        ParameterExceptionUtil.verify("tutorial.type", e.getType()).isNotNull();
        if (e.getFilesrc() != null) ParameterExceptionUtil.verify("tutorial.filesrc", e.getFilesrc()).isLengthIn(0, 200);
        service.save(e);
        return Rest.createSuccess(e.getId());
    }

    @DeleteMapping("/mg/tutorial/{id}")
    public Rest<Boolean> delete(Tutorial e) throws ParameterException {
        ParameterExceptionUtil.verify("tutorial.id", e.getId()).isPositive();
        boolean success = service.delete(e);
        return Rest.createSuccess(success);
    }

    @PutMapping("/mg/tutorial/{id}")
    public Rest<Boolean> update(Tutorial e) throws ParameterException {
        ParameterExceptionUtil.verify("tutorial.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("tutorial.name | filesrc | type", e.getName(), e.getFilesrc(), e.getType()).atLeastOne().isNotEmpty();
        boolean success = service.update(e);
        return Rest.createSuccess(success);
    }

    @GetMapping({"/mg/tutorial/{id}"})
    public Rest<Tutorial> findById(Tutorial e) throws ParameterException {
        ParameterExceptionUtil.verify("tutorial.id", e.getId()).isPositive();
        Tutorial data = service.findById(e);
        return Rest.createSuccess(data);
    }

    @GetMapping({"/mg/tutorial/count"})
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/mg/tutorial"})
    public Rest<List<Tutorial>> find(SearchCondition searchCondition) {
        List<Tutorial> list = service.find(searchCondition);
        return Rest.createSuccess(list);
    }
}
