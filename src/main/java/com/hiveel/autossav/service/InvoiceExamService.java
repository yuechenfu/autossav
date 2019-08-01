package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.InvoiceExam;

import java.util.List;

public interface InvoiceExamService {
    int save(InvoiceExam e);

    boolean delete(InvoiceExam e);   

    int deleteAll();

    boolean update(InvoiceExam e);

    InvoiceExam findById(InvoiceExam e);

    int count(SearchCondition searchCondition);
    
    List<InvoiceExam> find(SearchCondition searchCondition);

}
