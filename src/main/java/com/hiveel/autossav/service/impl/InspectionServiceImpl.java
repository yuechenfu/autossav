package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.InspectionDao;
import com.hiveel.autossav.dao.VehicleDao;
import com.hiveel.autossav.dao.PersonDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Inspection;
import com.hiveel.autossav.model.entity.InspectionStatus;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InspectionServiceImpl implements InspectionService {
    @Autowired
    private InspectionDao dao;
    
    @Autowired
    private VehicleDao vehicleDao;
    
    @Autowired
    private PersonDao personDao;     

    @Override
    public int save(Inspection e) {
    	e.fillNotRequire();
    	e.setStatus(InspectionStatus.COMPLETE);
    	e.createAt();
    	e.updateAt();    	    	
        return dao.save(e);
    }
    @Override
    public int saveByPlanJob(Inspection e) {
    	e.fillNotRequire();
    	e.setStatus(InspectionStatus.PENDING);
    	e.createAt();
    	e.updateAt();    	    	
        return dao.save(e);
    }
    @Override
    public boolean delete(Inspection e) {
        return dao.delete(e)==1;
    }

    @Override
    public boolean update(Inspection e) {    	
    	e.updateAt();    	    	
        return dao.update(e)==1;
    }
    @Override
    public boolean updateStatusByPendingAndDate(Inspection e) {    
    	e.updateAt(); 
        return dao.updateStatusByPendingAndDate(e) >0;
    }
    @Override
    public Inspection findById(Inspection e) {
        Inspection result = dao.findById(e);
        return result != null ? result : Inspection.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<Inspection> find(SearchCondition searchCondition) {
    	searchCondition.setDefaultSortBy("updateAt");
        return dao.find(searchCondition);
    }
    
    @Override
    public int countByVehicle(SearchCondition searchCondition, Vehicle vehicle) {
        return dao.countByVehicle(searchCondition, vehicle);
    }
    
    @Override
    public List<Inspection> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) {   
    	searchCondition.setDefaultSortBy("updateAt");
    	return dao.findByVehicle(searchCondition, vehicle);
    }
    
    @Override
    public int countByDriver(SearchCondition searchCondition, Person driver) {
        return dao.countByDriver(searchCondition, driver);
    }
    
    @Override
    public List<Inspection> findByDriver(SearchCondition searchCondition, Person driver) {  
    	searchCondition.setDefaultSortBy("updateAt");
    	return dao.findByDriver(searchCondition, driver);
    }

}
