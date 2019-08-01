package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.VehicleGroup;

import java.util.List;

public interface VehicleService {
    int save(Vehicle e);

    boolean delete(Vehicle e);

    boolean update(Vehicle e);

    Vehicle findById(Vehicle e);

    int count(SearchCondition searchCondition);
    
    List<Vehicle> find(SearchCondition searchCondition);

    List<Vehicle> findByGroup(SearchCondition searchCondition, VehicleGroup group);
    
    int countExceptVehicleList(SearchCondition searchCondition, List<Vehicle> vehicleList);
    
    List<Vehicle> findExceptVehicleList(SearchCondition searchCondition, List<Vehicle> vehicleList);
}
