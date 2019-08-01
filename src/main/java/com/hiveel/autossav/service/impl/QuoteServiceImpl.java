package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.ProblemDao;
import com.hiveel.autossav.dao.QuoteDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuoteServiceImpl implements QuoteService {
    @Autowired
    private QuoteDao dao;
    @Autowired
    private ProblemDao problemDao;

    @Override
    public int save(Quote e) {
        e.fillNotRequire();
        e.updateAt();
        return dao.save(e);
    }

    @Override
    public boolean delete(Quote e) {
        return dao.delete(e) == 1;
    }

    @Override
    public boolean update(Quote e) {
        e.updateAt();
        return dao.update(e) == 1;
    }

    @Override
    public Quote findById(Quote e) {
        Quote result = dao.findById(e);
        return result != null ? result : Quote.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<Quote> find(SearchCondition searchCondition) {
    	searchCondition.setDefaultSortBy("updateAt");
    	return dao.find(searchCondition);        
    }

    @Override
    public List<Quote> findByInspection(SearchCondition searchCondition, Inspection inspection) {
    	searchCondition.setDefaultSortBy("updateAt");
        List<Problem> list = problemDao.findByInspection(searchCondition, inspection);
        return findByProblemList(searchCondition, list);
    }

    @Override
    public List<Quote> findByIssue(SearchCondition searchCondition, Issue issue) {
    	searchCondition.setDefaultSortBy("updateAt");
        List<Problem> problemList = problemDao.findByIssue(searchCondition, issue);
        return findByProblemList(searchCondition, problemList);
    }
    
    @Override
    public boolean deleteByProblem(Problem problem) {
        return dao.deleteByProblem(problem) > 1;
    }

    @Override
    public List<Quote> findByProblem(SearchCondition searchCondition, Problem problem) {
    	searchCondition.setDefaultSortBy("updateAt");
        return dao.findByProblem(searchCondition, problem);
    }

    @Override
    public List<Quote> findByProblemList(SearchCondition searchCondition, List<Problem> problemList) {
    	return dao.findByProblemList(searchCondition, problemList);
    }


}
