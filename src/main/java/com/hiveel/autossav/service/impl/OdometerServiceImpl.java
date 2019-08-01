package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.OdometerDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.OdometerService;
import com.hiveel.autossav.service.InspectionService;
import com.hiveel.autossav.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OdometerServiceImpl implements OdometerService {
    @Autowired
    private OdometerDao dao;
    @Autowired
    private InspectionService inspectionService;    
    @Autowired
    private IssueService issueService;        

    @Override
    public int save(Odometer e) {
        e.updateAt();
        return dao.save(e);
    }

    @Override
    public boolean delete(Odometer e) {
        return dao.delete(e) == 1;
    }

    @Override
    public boolean update(Odometer e) {
        e.updateAt();
        return dao.update(e) == 1;
    }

    @Override
    public Odometer findById(Odometer e) {
        Odometer result = dao.findById(e);
        if(result == null) return Odometer.NULL ;
        setRelate(result);
        return result;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<Odometer> find(SearchCondition searchCondition) {
    	searchCondition.setDefaultSortBy("updateAt");
        List<Odometer> list = dao.find(searchCondition);
        list.stream().forEach(e -> setRelate(e));
        return list;
    }
    @Override
    public Odometer findByInspection(SearchCondition searchCondition, Inspection inspection) {
    	searchCondition.setDefaultSortBy("updateAt");
        return dao.findByInspection(searchCondition, inspection);
    }  
 
    @Override
    public Odometer findByIssue(SearchCondition searchCondition, Issue issue) {
    	searchCondition.setDefaultSortBy("updateAt");
        return dao.findByIssue(searchCondition, issue);
    }
    
    @Override
    public int countByVehicle(SearchCondition searchCondition, Vehicle vehicle) {
        return dao.countByVehicle(searchCondition, vehicle);
    }

    @Override
    public List<Odometer> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) {
    	searchCondition.setDefaultSortBy("updateAt");
        List<Odometer> list = dao.findByVehicle(searchCondition, vehicle);
        list.stream().forEach(e -> setRelate(e));
        return list;
    }

    private void setRelate(Odometer e) {
        if (e.getType() == OdometerType.INSPECTION) {
            e.setInspection(new Inspection.Builder().set("id",e.getRelateId()).build());
        } else if (e.getType() == OdometerType.ISSUE) {
            e.setIssue(new Issue.Builder().set("id",e.getRelateId()).build());
        }
    }

}
