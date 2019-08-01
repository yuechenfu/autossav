package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.VehicleGroup;

import java.util.List;

public interface VehicleGroupService {
    int save(VehicleGroup e);

    boolean delete(VehicleGroup e);

    boolean update(VehicleGroup e);

    VehicleGroup findById(VehicleGroup e);

    int count(SearchCondition searchCondition);
    
    List<VehicleGroup> find(SearchCondition searchCondition);

}
