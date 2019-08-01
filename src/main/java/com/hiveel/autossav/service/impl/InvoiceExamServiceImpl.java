package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.InvoiceExamDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.InvoiceExam;
import com.hiveel.autossav.service.InvoiceExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceExamServiceImpl implements InvoiceExamService {
    @Autowired
    private InvoiceExamDao dao;

    @Override
    public int save(InvoiceExam e) {
        e.updateAt(); 
        return dao.save(e);
    }

    @Override
    public boolean delete(InvoiceExam e) {
        return dao.delete(e) == 1;
    }
    
    @Override
    public int deleteAll() {
        return dao.deleteAll();
    }

    @Override
    public boolean update(InvoiceExam e) {
    	e.updateAt(); 
        return dao.update(e) == 1;
    }

    @Override
    public InvoiceExam findById(InvoiceExam e) {
    	InvoiceExam result = dao.findById(e);
        return result != null ? result : InvoiceExam.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<InvoiceExam> find(SearchCondition searchCondition) {
    	searchCondition.setDefaultSortBy("id");
        return dao.find(searchCondition);
    }
}
