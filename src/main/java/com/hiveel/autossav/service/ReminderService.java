package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.Reminder;

import java.util.List;

public interface ReminderService {
    int save(Reminder e);

    boolean delete(Reminder e);

    boolean update(Reminder e);

    Reminder findById(Reminder e);

    int count(SearchCondition searchCondition);
    
    int countByDriver(SearchCondition searchCondition, Person driver);
    
    List<Reminder> find(SearchCondition searchCondition);

    List<Reminder> findByVehicle(SearchCondition searchCondition, Vehicle vehicle);
    
    List<Reminder> findByDriver(SearchCondition searchCondition, Person driver);
}
