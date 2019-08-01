package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.PlanDao;
import com.hiveel.autossav.dao.VehicleDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Plan;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {
    @Autowired
    private PlanDao dao;
    @Autowired
    private VehicleDao vehicleDao;

    @Override
    public int save(Plan e) {
        e.updateAt();
        return dao.save(e);
    }

    @Override
    public boolean delete(Plan e) {
        return dao.delete(e)==1;
    }

    @Override
    public boolean update(Plan e) {
        e.updateAt();
        return dao.update(e)==1;
    }

    @Override
    public Plan findById(Plan e) {
        Plan result = dao.findById(e);
        return result != null ? result : Plan.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<Plan> find(SearchCondition searchCondition) {
        searchCondition.setDefaultSortBy("day", true);
        return dao.find(searchCondition);
    }
    
    @Override
    public List<Plan> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) {   
    	searchCondition.setDefaultSortBy("updateAt");
    	return dao.findByVehicle(searchCondition, vehicle);
    }
}
