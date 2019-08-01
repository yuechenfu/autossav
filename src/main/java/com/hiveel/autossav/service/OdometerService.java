package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Inspection;
import com.hiveel.autossav.model.entity.Issue;
import com.hiveel.autossav.model.entity.Odometer;
import com.hiveel.autossav.model.entity.Vehicle;

import java.util.List;

public interface OdometerService {
    int save(Odometer e);

    boolean delete(Odometer e);

    boolean update(Odometer e);

    Odometer findById(Odometer e);

    int count(SearchCondition searchCondition);

    List<Odometer> find(SearchCondition searchCondition);

    Odometer findByInspection(SearchCondition searchCondition, Inspection inspection);

    Odometer findByIssue(SearchCondition searchCondition, Issue issue);   
    
    int countByVehicle(SearchCondition searchCondition, Vehicle vehicle);

    List<Odometer> findByVehicle(SearchCondition searchCondition, Vehicle vehicle);
}
