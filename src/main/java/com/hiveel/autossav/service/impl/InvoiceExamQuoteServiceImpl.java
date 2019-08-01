package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.InvoiceExamQuoteDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.InvoiceExamQuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceExamQuoteServiceImpl implements InvoiceExamQuoteService {
    @Autowired
    private InvoiceExamQuoteDao dao;

    @Override
    public int save(InvoiceExamQuote e) {
        e.fillNotRequire();
        e.updateAt();
        return dao.save(e);
    }

    @Override
    public boolean delete(InvoiceExamQuote e) {
        return dao.delete(e) == 1;
    }    

    @Override
    public int deleteAll() {
        return dao.deleteAll();
    }

    @Override
    public boolean update(InvoiceExamQuote e) {
        e.updateAt();
        return dao.update(e) == 1;
    }

    @Override
    public InvoiceExamQuote findById(InvoiceExamQuote e) {
        InvoiceExamQuote result = dao.findById(e);
        return result != null ? result : InvoiceExamQuote.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<InvoiceExamQuote> find(SearchCondition searchCondition) {
    	searchCondition.setDefaultSortBy("updateAt");
    	return dao.find(searchCondition);        
    }

}
