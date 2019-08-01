package com.hiveel.autossav.controller.rest.dr;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.ExamService;
import com.hiveel.autossav.service.InspectionService;
import com.hiveel.autossav.service.IssueService;
import com.hiveel.autossav.service.ProblemService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController(value="DriverProblemController")
public class ProblemController {
    @Autowired
    private ProblemService service;
    @Autowired
    private InspectionService inspectionService;
    @Autowired
    private IssueService issueService;
    @Autowired
    private ExamService examService;

    @PostMapping({"/dr/problem"})
    public Rest<Long> save(Problem e) throws ParameterException {
        ParameterExceptionUtil.verify("problem.relateId", e.getRelateId()).isPositive();
        ParameterExceptionUtil.verify("problem.type", e.getType()).isNotNull();
        e.setExam(new Exam.Builder().set("id", 0L).build());
        Problem data = service.findByRelatedIdAndExamId(e);
        if (data.isNull()) service.save(e);
        else {
        	e.setId(data.getId());
        	service.update(e);
        }
        return Rest.createSuccess(e.getId());
    }

    @GetMapping({"/dr/problem/{id}"})
    public Rest<Problem> findById(Problem e) throws ParameterException {
        ParameterExceptionUtil.verify("problem.id", e.getId()).isPositive();
        Problem data = service.findById(e);
        return Rest.createSuccess(data);
    }

    @GetMapping({"/dr/problem/count"})
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }
   
    @GetMapping({"/dr/problem"})
    public Rest<List<Problem>> find(SearchCondition searchCondition) {
        List<Problem> list = service.find(searchCondition);
        return Rest.createSuccess(list);
    }
    
    @GetMapping({ "/dr/inspection/{id}/problem"})
    public Rest<List<Problem>> findByInspection(SearchCondition searchCondition, Inspection inspection) throws ParameterException {
        ParameterExceptionUtil.verify("inspection.id", inspection.getId()).isPositive();
        List<Problem> list = service.findByInspection(searchCondition, inspection);
        return Rest.createSuccess(list);
    }

    @GetMapping({"/dr/issue/{id}/problem"})
    public Rest<List<Problem>> findByIssue(SearchCondition searchCondition, Issue issue) throws ParameterException {
        ParameterExceptionUtil.verify("issue.id", issue.getId()).isPositive();
        List<Problem> list = service.findByIssue(searchCondition, issue);
        return Rest.createSuccess(list);
    }

    @GetMapping({"/dr/vehicle/{id}/problem"})
    public Rest<List<Problem>> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        List<Problem> result =  service.findByVehicle(searchCondition,vehicle);
        return Rest.createSuccess(result);
    }


}

