package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.VehicleDriverRelateDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.VehicleDriverRelate;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.service.VehicleDriverRelateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleDriverRelateServiceImpl implements VehicleDriverRelateService {
    @Autowired
    private VehicleDriverRelateDao dao;   

    @Override
    public int save(VehicleDriverRelate e) {
    	e.setOffDate("");
    	e.setOffOdometer("");
    	e.updateAt();    	    	
        return dao.save(e);
    }

    @Override
    public boolean delete(VehicleDriverRelate e) {
        return dao.delete(e)==1;
    }

    @Override
    public boolean update(VehicleDriverRelate e) {    	    	    	    	
    	e.updateAt();
        return dao.update(e)==1;
    }

    @Override
    public VehicleDriverRelate findById(VehicleDriverRelate e) {
        VehicleDriverRelate result = dao.findById(e);
        return result != null ? result : VehicleDriverRelate.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<VehicleDriverRelate> find(SearchCondition searchCondition) {
    	searchCondition.setDefaultSortBy("updateAt");
        return dao.find(searchCondition);
    }
    
    @Override
    public int countByVehicle(SearchCondition searchCondition, Vehicle vehicle) {
        return dao.countByVehicle(searchCondition, vehicle);
    }
    
    @Override
    public List<VehicleDriverRelate> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) {   
    	searchCondition.setDefaultSortBy("updateAt");
    	return dao.findByVehicle(searchCondition, vehicle);
    }
        
    @Override
    public int countByDriver(SearchCondition searchCondition, Person driver) {
        return dao.countByDriver(searchCondition, driver);
    }
    
    @Override
    public List<VehicleDriverRelate> findByDriver(SearchCondition searchCondition, Person driver) {  
    	searchCondition.setDefaultSortBy("updateAt");
    	return dao.findByDriver(searchCondition, driver);
    }
    
    @Override
    public List<VehicleDriverRelate> findByDriverAndVehicle(SearchCondition searchCondition, Person driver, Vehicle vehicle) {  
    	searchCondition.setDefaultSortBy("updateAt");
    	return dao.findByDriverAndVehicle(searchCondition, driver, vehicle);
    }
}
