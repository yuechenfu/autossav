package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.Inspection;
import com.hiveel.autossav.model.entity.InspectionStatus;

import java.util.List;

public interface InspectionService {
    int save(Inspection e);
    
    int saveByPlanJob(Inspection e);

    boolean delete(Inspection e);

    boolean update(Inspection e);

    boolean updateStatusByPendingAndDate(Inspection e);

    Inspection findById(Inspection e);

    int count(SearchCondition searchCondition);
    
    List<Inspection> find(SearchCondition searchCondition);

    int countByVehicle(SearchCondition searchCondition, Vehicle vehicle);
    
    List<Inspection> findByVehicle(SearchCondition searchCondition, Vehicle vehicle);
    
    int countByDriver(SearchCondition searchCondition, Person driver);
    
    List<Inspection> findByDriver(SearchCondition searchCondition, Person driver);
}
