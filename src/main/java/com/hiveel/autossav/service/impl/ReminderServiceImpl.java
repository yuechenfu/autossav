package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.ReminderDao;
import com.hiveel.autossav.dao.VehicleDao;
import com.hiveel.autossav.dao.VehicleDriverRelateDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.Reminder;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.VehicleDriverRelate;
import com.hiveel.autossav.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderServiceImpl implements ReminderService {
    @Autowired
    private ReminderDao dao;
    
    @Autowired
    private VehicleDao vehicleDao;  
    
    @Autowired
    private VehicleDriverRelateDao vehicleDriverRelateDao;     

    @Override
    public int save(Reminder e) {
    	e.fillNotRequire();
    	e.createAt();
    	e.updateAt();    	    	
        return dao.save(e);
    }

    @Override
    public boolean delete(Reminder e) {
        return dao.delete(e)==1;
    }

    @Override
    public boolean update(Reminder e) {    	
    	e.updateAt();    	    	
        return dao.update(e)==1;
    }

    @Override
    public Reminder findById(Reminder e) {
        Reminder result = dao.findById(e);
        return result != null ? result : Reminder.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<Reminder> find(SearchCondition searchCondition) {
    	searchCondition.setDefaultSortBy("updateAt");
        return dao.find(searchCondition);
    }
    
    @Override
    public List<Reminder> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) {   
    	searchCondition.setDefaultSortBy("updateAt");
    	return dao.findByVehicle(searchCondition, vehicle);
    }
    
    @Override
    public List<Reminder> findByDriver(SearchCondition searchCondition, Person driver) {
    	searchCondition.setDefaultSortBy("updateAt");
    	List<VehicleDriverRelate> vehicleDriverRelateList = vehicleDriverRelateDao.findByDriver(searchCondition, driver);
    	List<Reminder> result = vehicleDriverRelateList.stream().flatMap(e -> dao.findByVehicle(searchCondition, e.getVehicle()).stream()).collect(Collectors.toList());
        return result;
    }
    
    @Override
    public int countByDriver(SearchCondition searchCondition, Person driver) {
    	SearchCondition vehicleDriverRelateSearchCondition = new SearchCondition();
    	vehicleDriverRelateSearchCondition.setLimit(999);
    	List<VehicleDriverRelate> vehicleDriverRelateList = vehicleDriverRelateDao.findByDriver(vehicleDriverRelateSearchCondition, driver);
    	List<Vehicle> vehicleList = vehicleDriverRelateList.stream().map(e->e.getVehicle()).distinct().collect(Collectors.toList());
    	return dao.countByVehicleList(vehicleDriverRelateSearchCondition, vehicleList);
    }
}
