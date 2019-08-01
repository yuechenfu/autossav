package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.ExamDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Exam;
import com.hiveel.autossav.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {
    @Autowired
    private ExamDao dao;

    @Override
    public int save(Exam e) {
        e.fillNotRequire();
        e.updateAt();
        return dao.save(e);
    }

    @Override
    public boolean delete(Exam e) {
        return dao.delete(e) == 1;
    }

    @Override
    public boolean update(Exam e) {
        e.updateAt();
        return dao.update(e) == 1;
    }

    @Override
    public Exam findById(Exam e) {
        Exam result = dao.findById(e);
        return result != null ? result : Exam.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<Exam> find(SearchCondition searchCondition) {
        searchCondition.setDefaultSortBy("name", true);
        return dao.find(searchCondition);
    }
    
}
