package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.VehicleDriverRelate;

import java.util.List;

public interface VehicleDriverRelateService {
    int save(VehicleDriverRelate e);

    boolean delete(VehicleDriverRelate e);

    boolean update(VehicleDriverRelate e);

    VehicleDriverRelate findById(VehicleDriverRelate e);

    int count(SearchCondition searchCondition);
    
    List<VehicleDriverRelate> find(SearchCondition searchCondition);

    int countByVehicle(SearchCondition searchCondition, Vehicle vehicle);
    
    List<VehicleDriverRelate> findByVehicle(SearchCondition searchCondition, Vehicle vehicle);
    
    int countByDriver(SearchCondition searchCondition, Person driver);
    
    List<VehicleDriverRelate> findByDriver(SearchCondition searchCondition, Person driver);
    
    List<VehicleDriverRelate> findByDriverAndVehicle(SearchCondition searchCondition, Person driver, Vehicle vehicle);   
}
