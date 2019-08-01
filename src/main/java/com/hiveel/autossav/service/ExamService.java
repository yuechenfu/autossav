package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Exam;

import java.util.List;

public interface ExamService {
    int save(Exam e);

    boolean delete(Exam e);

    boolean update(Exam e);

    Exam findById(Exam e);

    int count(SearchCondition searchCondition);
    
    List<Exam> find(SearchCondition searchCondition);
}
