package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Inspection;
import com.hiveel.autossav.model.entity.Issue;
import com.hiveel.autossav.model.entity.Problem;
import com.hiveel.autossav.model.entity.Vehicle;

import java.util.List;

public interface ProblemService {
    int save(Problem e);

    boolean delete(Problem e);

    boolean update(Problem e);

    Problem findById(Problem e);
    
    Problem findByRelatedIdAndExamId(Problem e);

    int count(SearchCondition searchCondition);   

    List<Problem> find(SearchCondition searchCondition);
    
    int countByInspection(SearchCondition searchCondition, Inspection inspection);

    List<Problem> findByInspection(SearchCondition searchCondition, Inspection inspection);
    
    List<Problem> findByInspectionList(SearchCondition searchCondition,List<Inspection> inspectionList);
    
    int countByIssue(SearchCondition searchCondition, Issue issue);

    List<Problem> findByIssue(SearchCondition searchCondition, Issue issue);
    
    List<Problem> findByIssueList(SearchCondition searchCondition,List<Issue> issueList);

    List<Problem> findByVehicle(SearchCondition searchCondition,Vehicle vehicle);
    
    List<Problem> findByVehicleList(SearchCondition searchCondition,List<Vehicle> vehicleList);

}
