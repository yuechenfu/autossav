package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Plan;
import com.hiveel.autossav.model.entity.Vehicle;

import java.util.List;

public interface PlanService {
    int save(Plan e);

    boolean delete(Plan e);

    boolean update(Plan e);

    Plan findById(Plan e);

    int count(SearchCondition searchCondition);
    
    List<Plan> find(SearchCondition searchCondition);

    List<Plan> findByVehicle(SearchCondition searchCondition, Vehicle vehicle);
}
