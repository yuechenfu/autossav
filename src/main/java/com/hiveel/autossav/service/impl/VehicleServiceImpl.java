package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.VehicleDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.VehicleGroup;
import com.hiveel.autossav.model.entity.VehicleStatus;
import com.hiveel.autossav.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {
    @Autowired
    private VehicleDao dao;

    @Override
    public int save(Vehicle e) {
    	e.fillNotRequire();
    	e.createAt();
    	e.updateAt(); 
    	e.setStatus(VehicleStatus.INACTIVE);
        return dao.save(e);
    }

    @Override
    public boolean delete(Vehicle e) {
        return dao.delete(e)==1;
    }

    @Override
    public boolean update(Vehicle e) {
    	e.updateAt();      	
        return dao.update(e)==1;
    }

    @Override
    public Vehicle findById(Vehicle e) {
        Vehicle result = dao.findById(e);
        return result != null ? result : Vehicle.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<Vehicle> find(SearchCondition searchCondition) {
    	searchCondition.setDefaultSortBy("updateAt");
        return dao.find(searchCondition);
    }
    
    @Override
    public List<Vehicle> findByGroup(SearchCondition searchCondition, VehicleGroup group) { 
    	searchCondition.setDefaultSortBy("updateAt");
    	return dao.findByGroup(searchCondition, group);
    }
    
    @Override
    public int countExceptVehicleList(SearchCondition searchCondition, List<Vehicle> vehicleList) {
        return dao.countExceptVehicleList(searchCondition, vehicleList);
    }
    
    @Override
    public List<Vehicle> findExceptVehicleList(SearchCondition searchCondition, List<Vehicle> vehicleList) {
    	searchCondition.setDefaultSortBy("updateAt");
    	return dao.findExceptVehicleList(searchCondition, vehicleList);
    }
}
