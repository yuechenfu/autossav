package com.hiveel.autossav.controller.rest.mg;

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

@RestController
public class ProblemController {
    @Autowired
    private ProblemService service;
    @Autowired
    private InspectionService inspectionService;
    @Autowired
    private IssueService issueService;
    @Autowired
    private ExamService examService;

    @PostMapping({"/mg/problem"})
    public Rest<Long> save(Problem e) throws ParameterException {
        ParameterExceptionUtil.verify("problem.relateId", e.getRelateId()).isPositive();
        ParameterExceptionUtil.verify("problem.type", e.getType()).isNotNull();
        ParameterExceptionUtil.verify("problem.exam", e.getExam()).isNotNull();
        ParameterExceptionUtil.verify("problem.exam.id", e.getExam().getId()).isPositive();
        Problem data = service.findByRelatedIdAndExamId(e); 
        if (data.isNull()) service.save(e);
        else {
        	e.setId(data.getId());
        	service.update(e); 
        }        
        return Rest.createSuccess(e.getId());
    }

    @DeleteMapping({"/mg/problem/{id}"})
    public Rest<Boolean> delete(Problem e) throws ParameterException {
        ParameterExceptionUtil.verify("problem.id", e.getId()).isPositive();
        boolean success = service.delete(e);
        return Rest.createSuccess(success);
    }

    @PutMapping("/mg/problem/{id}")
    public Rest<Boolean> update(Problem e) throws ParameterException {
        ParameterExceptionUtil.verify("problem.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("problem.relatedId | remark | type | imgsrc1", e.getRelateId(), e.getRemark(), e.getType(), e.getImgsrc1()).atLeastOne().isNotEmpty();
        boolean success = service.update(e);
        if ( success ) {
        	Problem data = service.findById(e);
        	if (data.getType() == ProblemType.INSPECTION) {
            	Inspection inspection=new Inspection.Builder().set("id",data.getRelateId()).build();
            	inspection.setStatus(InspectionStatus.QUOTED);
            	inspectionService.update(inspection);
        	}else if (data.getType() == ProblemType.ISSUE) {
            	Issue issue=new Issue.Builder().set("id",data.getRelateId()).build();
            	issue.setStatus(IssueStatus.QUOTED);
            	issueService.update(issue);
            }
        }
        return Rest.createSuccess(success);
    }

    @GetMapping({"/mg/problem/{id}"})
    public Rest<Problem> findById(Problem e) throws ParameterException {
        ParameterExceptionUtil.verify("problem.id", e.getId()).isPositive();
        Problem data = service.findById(e);
        return Rest.createSuccess(data);
    }

    @GetMapping({"/mg/problem/count"})
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }
   
    @GetMapping({"/mg/problem"})
    public Rest<List<Problem>> find(SearchCondition searchCondition) {
        List<Problem> list = service.find(searchCondition);
        return Rest.createSuccess(list);
    }
    
    @GetMapping({"/mg/inspection/{id}/problem/count"})
    public Rest<Integer> countByInspection(SearchCondition searchCondition, Inspection inspection) throws ParameterException {
    	ParameterExceptionUtil.verify("inspection.id", inspection.getId()).isPositive();
        int count = service.countByInspection(searchCondition, inspection);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/mg/inspection/{id}/problem"})
    public Rest<List<Problem>> findByInspection(SearchCondition searchCondition, Inspection inspection) throws ParameterException {
        ParameterExceptionUtil.verify("inspection.id", inspection.getId()).isPositive();
        List<Problem> list = service.findByInspection(searchCondition, inspection);
        return Rest.createSuccess(list);
    }
    
    @GetMapping({"/mg/issue/{id}/problem/count"})
    public Rest<Integer> countByIssue(SearchCondition searchCondition, Issue issue) throws ParameterException {
    	ParameterExceptionUtil.verify("issue.id", issue.getId()).isPositive();
        int count = service.countByIssue(searchCondition, issue);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/mg/issue/{id}/problem"})
    public Rest<List<Problem>> findByIssue(SearchCondition searchCondition, Issue issue) throws ParameterException {
        ParameterExceptionUtil.verify("issue.id", issue.getId()).isPositive();
        List<Problem> list = service.findByIssue(searchCondition, issue);
        return Rest.createSuccess(list);
    }

    @GetMapping({"/mg/vehicle/{id}/problem"})
    public Rest<List<Problem>> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        List<Problem> result =  service.findByVehicle(searchCondition,vehicle);
        return Rest.createSuccess(result);
    }


}

