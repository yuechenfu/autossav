package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;

import java.util.List;

public interface InvoiceExamQuoteService {
    int save(InvoiceExamQuote e);

    boolean delete(InvoiceExamQuote e);    

    int deleteAll();

    boolean update(InvoiceExamQuote e);

    InvoiceExamQuote findById(InvoiceExamQuote e);

    int count(SearchCondition searchCondition);

    List<InvoiceExamQuote> find(SearchCondition searchCondition);

}
