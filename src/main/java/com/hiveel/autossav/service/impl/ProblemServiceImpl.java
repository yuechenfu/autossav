package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.ExamDao;
import com.hiveel.autossav.dao.InspectionDao;
import com.hiveel.autossav.dao.IssueDao;
import com.hiveel.autossav.dao.ProblemDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.ProblemService;
import com.hiveel.autossav.service.InspectionService;
import com.hiveel.autossav.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemServiceImpl implements ProblemService {
    @Autowired
    private ProblemDao dao;
    @Autowired
    private InspectionService inspectionService;    
    @Autowired
    private IssueService issueService;        
    @Autowired
    private ExamDao examDao;

    @Override
    public int save(Problem e) {
        e.fillNotRequire();
        e.setVehicle(findVehicle(e));
        e.updateAt();
        return dao.save(e);
    }

    @Override
    public boolean delete(Problem e) {
        return dao.delete(e) == 1;
    }

    @Override
    public boolean update(Problem e) {
        e.updateAt();
        return dao.update(e) == 1;
    }

    @Override
    public Problem findById(Problem e) {
        Problem result = dao.findById(e);
        if(result == null) return Problem.NULL ;
        findExam(result);
        return result;
    }
    
    @Override
    public Problem findByRelatedIdAndExamId(Problem e) {
        Problem result = dao.findByRelatedIdAndExamId(e);
        if(result == null) return Problem.NULL ;
        findExam(result);
        return result;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<Problem> find(SearchCondition searchCondition) {
    	searchCondition.setDefaultSortBy("updateAt");
        List<Problem> list = dao.find(searchCondition);
        list.stream().forEach(e -> findExam(e));
        return list;
    }
    
    @Override
    public int countByInspection(SearchCondition searchCondition, Inspection inspection) {
        return dao.countByInspection(searchCondition, inspection);
    }

    @Override
    public List<Problem> findByInspection(SearchCondition searchCondition, Inspection inspection) {
    	searchCondition.setDefaultSortBy("updateAt");
        List<Problem> list = dao.findByInspection(searchCondition, inspection);
        list.stream().forEach(e -> findExam(e));
        return list;
    }
    
    @Override
    public List<Problem> findByInspectionList(SearchCondition searchCondition,List<Inspection> inspectionList) {
    	searchCondition.setDefaultSortBy("updateAt");
        return dao.findByInspectionList(searchCondition, inspectionList);
    }
    
    @Override
    public int countByIssue(SearchCondition searchCondition, Issue issue) {
        return dao.countByIssue(searchCondition, issue);
    }

    @Override
    public List<Problem> findByIssue(SearchCondition searchCondition, Issue issue) {
    	searchCondition.setDefaultSortBy("updateAt");
        List<Problem> list = dao.findByIssue(searchCondition, issue);
        list.stream().forEach(e ->findExam(e));
        return list;
    }
    
    @Override
    public List<Problem> findByIssueList(SearchCondition searchCondition,List<Issue> issueList) {
        return dao.findByIssueList(searchCondition, issueList);
    }

    @Override
    public List<Problem> findByVehicle(SearchCondition searchCondition, Vehicle vehicle) {
        List<Problem> list = dao.findByVehicle(searchCondition, vehicle);
        list.stream().forEach(e -> findExam(e));
        return list;
    }
    
    @Override
    public List<Problem> findByVehicleList(SearchCondition searchCondition,List<Vehicle> vehicleList) {
        return dao.findByVehicleList(searchCondition, vehicleList);
    }

//    todo 设置内部值放到controller
    private void findExam(Problem e) {
        if (e.getExam().getId() != 0L) e.setExam(examDao.findById(e.getExam()));
        if (e.getType() == ProblemType.INSPECTION) {
            e.setInspection(new Inspection.Builder().set("id",e.getRelateId()).build());
        } else if (e.getType() == ProblemType.ISSUE) {
            e.setIssue(new Issue.Builder().set("id",e.getRelateId()).build());
        }
    }

    private Vehicle findVehicle(Problem e) {
    	switch (e.getType()) {
	    	case INSPECTION:
	    		return inspectionService.findById(new Inspection.Builder().set("id",e.getRelateId()).build()).getVehicle();
	    	case ISSUE: 
	    		return issueService.findById(new Issue.Builder().set("id",e.getRelateId()).build()).getVehicle();
    	}
    	return null;
    }

}
