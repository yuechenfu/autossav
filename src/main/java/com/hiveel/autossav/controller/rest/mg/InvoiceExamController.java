package com.hiveel.autossav.controller.rest.mg;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.InvoiceExam;
import com.hiveel.autossav.model.entity.InvoiceExamQuote;
import com.hiveel.autossav.service.InvoiceExamQuoteService;
import com.hiveel.autossav.service.InvoiceExamService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class InvoiceExamController {
    @Autowired
    private InvoiceExamService service;
    @Autowired
    private InvoiceExamQuoteService invoiceExamQuoteService;

    @GetMapping({"/mg/invoiceExam/{id}"})
    public Rest<InvoiceExam> findById(InvoiceExam e) throws ParameterException {
        ParameterExceptionUtil.verify("invoiceExam.id", e.getId()).isPositive();
        InvoiceExam data = service.findById(e);
        return Rest.createSuccess(data);
    }
    
    @GetMapping({"/mg/invoiceExam/{id}/quote"})
    public Rest<List<InvoiceExamQuote>> findByIdQuote(InvoiceExam e) throws ParameterException {
        ParameterExceptionUtil.verify("invoiceExam.id", e.getId()).isPositive();
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setLimit(0);
        searchCondition.setGroup(e.getId().toString());
        return Rest.createSuccess(invoiceExamQuoteService.find(searchCondition));
    }

    @GetMapping({"/mg/invoiceExam/count"})
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }
    @GetMapping({"/mg/invoiceExam"})
    public Rest<List<InvoiceExam>> find(SearchCondition searchCondition) {
        List<InvoiceExam> list = service.find(searchCondition);
        return Rest.createSuccess(list);
    }
}
