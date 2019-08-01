package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;

import java.util.List;

public interface QuoteService {
    int save(Quote e);

    boolean delete(Quote e);

    boolean update(Quote e);

    Quote findById(Quote e);

    int count(SearchCondition searchCondition);

    List<Quote> find(SearchCondition searchCondition);

    List<Quote> findByInspection(SearchCondition searchCondition, Inspection inspection);

    List<Quote> findByIssue(SearchCondition searchCondition,Issue issue);
    
    boolean deleteByProblem(Problem problem);

    List<Quote> findByProblem(SearchCondition searchCondition,Problem problem);
    
    List<Quote> findByProblemList(SearchCondition searchCondition, List<Problem> problemList);

}
