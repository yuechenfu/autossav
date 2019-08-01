package com.hiveel.autossav.controller.rest.mg;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.InspectionService;
import com.hiveel.autossav.service.IssueService;
import com.hiveel.autossav.service.ProblemService;
import com.hiveel.autossav.service.QuoteService;
import com.hiveel.autossav.service.VehicleService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.util.ThreadUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController("QuoteControllerMg")
public class QuoteController {
    @Autowired
    private QuoteService service;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private InspectionService inspectionService;
    @Autowired
    private IssueService issueService;
    @Autowired
    private VehicleService vehicleService;      

    @PostMapping("/mg/quote")
    public Rest<Long> save(Quote e) throws ParameterException {
        ParameterExceptionUtil.verify("quote.name", e.getName()).isLengthIn(1, 50);
        ParameterExceptionUtil.verify("quote.problem", e.getProblem()).isNotNull();
        ParameterExceptionUtil.verify("quote.problem.id", e.getProblem().getId()).isPositive();
        service.save(e);  
        ThreadUtil.run(()->updateQuotedStatus(e));
        return Rest.createSuccess(e.getId());
    }

    @DeleteMapping("/mg/quote/{id}")
    public Rest<Boolean> delete(Quote e) throws ParameterException {
        ParameterExceptionUtil.verify("quote.id", e.getId()).isPositive();        
        boolean success = service.delete(e);
        ThreadUtil.run(()->updateQuotedStatus(service.findById(e)));
        return Rest.createSuccess(success);
    }

    @PutMapping("/mg/quote/{id}")
    public Rest<Boolean> update(Quote e) throws ParameterException {
        ParameterExceptionUtil.verify("quote.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("quote.name | labor | part", e.getName(), e.getLabor(), e.getPart()).atLeastOne().isNotEmpty();
        boolean success = service.update(e);
        ThreadUtil.run(()->updateQuotedStatus(service.findById(e)));
        return Rest.createSuccess(success);
    }
    
    private boolean updateQuotedStatus(Quote e) {
    	boolean result = false;
    	Problem problem = problemService.findById(e.getProblem());
        switch (problem.getType()) {
            case INSPECTION:
            	result = inspectionService.update(new Inspection.Builder().set("id", problem.getRelateId()).set("status", InspectionStatus.QUOTED).build()); break;            	
            case ISSUE:
            	result = issueService.update(new Issue.Builder().set("id", problem.getRelateId()).set("status", IssueStatus.QUOTED).build()); break;   
        }
        return result;
    }

    @GetMapping({"/mg/quote/{id}"})
    public Rest<Quote> findById(Quote e) throws ParameterException {
        ParameterExceptionUtil.verify("quote.id", e.getId()).isPositive();
        Quote data = service.findById(e);
        data.setProblem(problemService.findById(data.getProblem()));
        return Rest.createSuccess(data);
    }

    @GetMapping({"/mg/quote/count"})
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }

    @GetMapping({"/mg/quote"})
    public Rest<List<Quote>> find(SearchCondition searchCondition) {
        List<Quote> list = service.find(searchCondition);
        list.stream().forEach(e->e.setProblem(problemService.findById(e.getProblem())));
        return Rest.createSuccess(list);
    }
    
    @GetMapping({"/mg/quote/cost"})
    public Rest<Double> findCost(SearchCondition searchCondition) {
    	searchCondition.setLimit(0);
        List<Quote> list = service.find(searchCondition);
        return Rest.createSuccess(Double.valueOf(list.stream().mapToDouble(e->e.getLabor()+e.getPart()).sum()));
    }
    
    @GetMapping({"/mg/vehicle/own/quote/cost"})
    public Rest<Double> findOwnVehicleQuoteCost(SearchCondition searchCondition) {
    	searchCondition.setRental(false);
    	searchCondition.setLimit(0);
    	List<Vehicle> vehicleList = vehicleService.find(searchCondition);
    	List<Problem> problemList = problemService.findByVehicleList(searchCondition, vehicleList);
    	List<Quote> list = service.findByProblemList(searchCondition, problemList);
    	return Rest.createSuccess(Double.valueOf(list.stream().mapToDouble(e->e.getLabor()+e.getPart()).sum()));
    }
    
    @GetMapping({"/mg/vehicle/rental/quote/cost"})
    public Rest<Double> findRentalVehicleQuoteCost(SearchCondition searchCondition) {
    	searchCondition.setRental(true);
    	searchCondition.setLimit(0);
    	List<Vehicle> vehicleList = vehicleService.find(searchCondition);
    	List<Problem> problemList = problemService.findByVehicleList(searchCondition, vehicleList);
    	List<Quote> list = service.findByProblemList(searchCondition, problemList);
        return Rest.createSuccess(Double.valueOf(list.stream().mapToDouble(e->e.getLabor()+e.getPart()).sum()));
    }
    
    @GetMapping({"/mg/person/{id}/quote/cost"})
    public Rest<Double> findPersonQuoteCost(SearchCondition searchCondition, Person person) throws ParameterException {
    	ParameterExceptionUtil.verify("person.id", person.getId()).isPositive();
    	SearchCondition commonSearchCondition = new SearchCondition();
    	commonSearchCondition.setLimit(0);
    	List<Inspection> inspectionList = inspectionService.findByDriver(commonSearchCondition, person);
    	List<Issue> issueList = issueService.findByDriver(commonSearchCondition, person);
    	List<Problem> inspectionProblemList = problemService.findByInspectionList(commonSearchCondition, inspectionList);
    	List<Problem> issueProblemList = problemService.findByIssueList(commonSearchCondition, issueList);
    	List<Problem> problemList = Stream.of(inspectionProblemList, issueProblemList).flatMap(Collection::stream).collect(Collectors.toList());
    	List<Quote> list = service.findByProblemList(searchCondition, problemList);
        return Rest.createSuccess(Double.valueOf(list.stream().mapToDouble(e->e.getLabor()+e.getPart()).sum()));
    }

    @GetMapping({"/mg/inspection/{id}/quote"})
    public Rest<List<Quote>> findByInspection(SearchCondition searchCondition, Inspection inspection) throws ParameterException {
        ParameterExceptionUtil.verify("inspection.id", inspection.getId()).isPositive();
        List<Quote> list = service.findByInspection(searchCondition, inspection);
        return Rest.createSuccess(list);
    }

    @GetMapping({"/mg/issue/{id}/quote"})
    public Rest<List<Quote>> findByIssue(SearchCondition searchCondition, Issue issue) throws ParameterException {
        ParameterExceptionUtil.verify("issue.id", issue.getId()).isPositive();
        List<Quote> list = service.findByIssue(searchCondition, issue);
        return Rest.createSuccess(list);
    }
    
    @DeleteMapping("/mg/problem/{id}/quote")
    public Rest<Boolean> deleteByProblem(Problem problem) throws ParameterException {
    	ParameterExceptionUtil.verify("problem.id", problem.getId()).isPositive();       
        boolean success = service.deleteByProblem(problem);
        return Rest.createSuccess(success);
    }

    @GetMapping({"/mg/problem/{id}/quote"})
    public Rest<List<Quote>> findByProblem(SearchCondition searchCondition, Problem problem) throws ParameterException {
        ParameterExceptionUtil.verify("problem.id", problem.getId()).isPositive();
        List<Quote> list = service.findByProblem(searchCondition, problem);
        return Rest.createSuccess(list);
    }

    @GetMapping({"/mg/vehicle/{id}/quote"})
    public Rest<List<Quote>> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        List<Problem> problemList = problemService.findByVehicle(searchCondition,vehicle);
        List<Quote> result = problemList.stream().flatMap(problem -> service.findByProblem(searchCondition, problem).stream()).collect(Collectors.toList());
        return Rest.createSuccess(result);
    }

    @GetMapping({"/mg/vehicle/{id}/quote/cost"})
    public Rest<Double> findByVehicleQuoteCost(SearchCondition searchCondition, Vehicle vehicle) throws ParameterException {
        ParameterExceptionUtil.verify("vehicle.id", vehicle.getId()).isPositive();
        List<Problem> problemList = problemService.findByVehicle(searchCondition,vehicle);
        List<Quote> list = problemList.stream().flatMap(problem -> service.findByProblem(searchCondition, problem).stream()).collect(Collectors.toList());
        return Rest.createSuccess(Double.valueOf(list.stream().mapToDouble(e->e.getLabor()+e.getPart()).sum()));
    }
}
