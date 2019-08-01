package com.hiveel.autossav.controller.rest.dr;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.InspectionService;
import com.hiveel.autossav.service.IssueService;
import com.hiveel.autossav.service.ProblemService;
import com.hiveel.autossav.service.QuoteService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController("QuoteControllerDr")
public class QuoteController {
    @Autowired
    private QuoteService service;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private InspectionService inspectionService;
    @Autowired
    private IssueService issueService;


    @GetMapping({"/dr/quote/{id}"})
    public Rest<Quote> findById(Quote e) throws ParameterException {
        ParameterExceptionUtil.verify("quote.id", e.getId()).isPositive();
        Quote data = service.findById(e);
        data.setProblem(problemService.findById(data.getProblem()));
        return Rest.createSuccess(data);
    }

    @GetMapping({"/dr/quote/count"})
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }

    @GetMapping({"/dr/quote"})
    public Rest<List<Quote>> find(SearchCondition searchCondition) {
        List<Quote> list = service.find(searchCondition);
        list.stream().forEach(e->e.setProblem(problemService.findById(e.getProblem())));
        return Rest.createSuccess(list);
    }

    @GetMapping({"/dr/inspection/{id}/quote"})
    public Rest<List<Quote>> findByInspection(SearchCondition searchCondition, Inspection inspection) throws ParameterException {
        ParameterExceptionUtil.verify("inspection.id", inspection.getId()).isPositive();
        List<Quote> list = service.findByInspection(searchCondition, inspection);
        return Rest.createSuccess(list);
    }

    @GetMapping({"/dr/issue/{id}/quote"})
    public Rest<List<Quote>> findByIssue(SearchCondition searchCondition, Issue issue) throws ParameterException {
        ParameterExceptionUtil.verify("issue.id", issue.getId()).isPositive();
        List<Quote> list = service.findByIssue(searchCondition, issue);
        return Rest.createSuccess(list);
    }

    @GetMapping({"/dr/problem/{id}/quote"})
    public Rest<List<Quote>> findByProblem(SearchCondition searchCondition, Problem problem) throws ParameterException {
        ParameterExceptionUtil.verify("problem.id", problem.getId()).isPositive();
        List<Quote> list = service.findByProblem(searchCondition, problem);
        return Rest.createSuccess(list);
    }

    @GetMapping({"/dr/vehicle/{id}/quote"})
    public Rest<List<Quote>> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        List<Problem> problemList = problemService.findByVehicle(searchCondition,vehicle);
        List<Quote> result = problemList.stream().flatMap(problem -> service.findByProblem(searchCondition, problem).stream()).collect(Collectors.toList());
        return Rest.createSuccess(result);
    }
}
