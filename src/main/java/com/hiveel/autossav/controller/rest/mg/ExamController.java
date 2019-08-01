package com.hiveel.autossav.controller.rest.mg;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Exam;
import com.hiveel.autossav.service.ExamService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExamController {
    @Autowired
    private ExamService service;

    @PostMapping("/mg/exam")
    public Rest<Long> save(Exam e) throws ParameterException {
        ParameterExceptionUtil.verify("exam.name", e.getName()).isLengthIn(1, 50);
        ParameterExceptionUtil.verify("exam.type", e.getType()).isNotNull();
        if (e.getContent() != null) ParameterExceptionUtil.verify("exam.content", e.getContent()).isLengthIn(0, 200);
        service.save(e);
        return Rest.createSuccess(e.getId());
    }

    @DeleteMapping("/mg/exam/{id}")
    public Rest<Boolean> delete(Exam e) throws ParameterException {
        ParameterExceptionUtil.verify("exam.id", e.getId()).isPositive();
        boolean success = service.delete(e);
        return Rest.createSuccess(success);
    }

    @PutMapping("/mg/exam/{id}")
    public Rest<Boolean> update(Exam e) throws ParameterException {
        ParameterExceptionUtil.verify("exam.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("exam.name | type | content", e.getName(), e.getType(), e.getContent()).atLeastOne().isNotEmpty();
        boolean success = service.update(e);
        return Rest.createSuccess(success);
    }

    @GetMapping({"/mg/exam/{id}"})
    public Rest<Exam> findById(Exam e) throws ParameterException {
        ParameterExceptionUtil.verify("exam.id", e.getId()).isPositive();
        Exam data = service.findById(e);
        return Rest.createSuccess(data);
    }

    @GetMapping({"/mg/exam/count"})
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/mg/exam"})
    public Rest<List<Exam>> find(SearchCondition searchCondition) {
        List<Exam> list = service.find(searchCondition);
        return Rest.createSuccess(list);
    }
}
