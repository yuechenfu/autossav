package com.hiveel.autossav.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hiveel.autossav.dao.IssueDao;
import com.hiveel.autossav.dao.PersonDao;
import com.hiveel.autossav.dao.VehicleDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Issue;
import com.hiveel.autossav.model.entity.IssueStatus;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.service.IssueService;

@Service
public class IssueServiceImpl implements IssueService {
    @Autowired
    private IssueDao dao;
    @Autowired
    private VehicleDao vehicleDao;
    @Autowired
    private PersonDao personDao;

    @Override
    public int save(Issue e) {
        e.fillNotRequire();
    	e.setStatus(IssueStatus.PENDING);
        e.createAt();
        e.updateAt();
        return dao.save(e);
    }

    @Override
    public boolean delete(Issue e) {
        return dao.delete(e)==1;
    }

    @Override
    public boolean update(Issue e) {
        e.updateAt();
        return dao.update(e)==1;
    }

    @Override
    public Issue findById(Issue e) {
        Issue result = dao.findById(e);
        return result != null ? result : Issue.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<Issue> find(SearchCondition searchCondition) {
        searchCondition.setDefaultSortBy("updateAt");
        return dao.find(searchCondition);
    }
    
    @Override
    public int countByVehicle(SearchCondition searchCondition, Vehicle vehicle) {
        return dao.countByVehicle(searchCondition, vehicle);
    }

    @Override
    public List<Issue> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) {        
        searchCondition.setDefaultSortBy("updateAt");
        return dao.findByVehicle(searchCondition, vehicle);
    }
    
    @Override
    public int countByDriver(SearchCondition searchCondition, Person driver) {
        return dao.countByDriver(searchCondition, driver);
    }
    
    @Override
    public List<Issue> findByDriver(SearchCondition searchCondition, Person driver) {        
        searchCondition.setDefaultSortBy("updateAt");
        return dao.findByDriver(searchCondition, driver);
    }
}
