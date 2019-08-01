package com.hiveel.autossav.service;

import java.util.List;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Issue;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.Vehicle;

public interface IssueService {
    int save(Issue e);

    boolean delete(Issue e);

    boolean update(Issue e);

    Issue findById(Issue e);

    int count(SearchCondition searchCondition);
    
    List<Issue> find(SearchCondition searchCondition);
    
    int countByVehicle(SearchCondition searchCondition, Vehicle vehicle);

    List<Issue> findByVehicle(SearchCondition searchCondition, Vehicle vehicle);
    
    int countByDriver(SearchCondition searchCondition, Person dirver);
    
    List<Issue> findByDriver(SearchCondition searchCondition, Person dirver);

}
