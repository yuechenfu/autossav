package com.hiveel.autossav.controller.rest.mg;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.InvoicePlatform;
import com.hiveel.autossav.service.InvoicePlatformService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InvoicePlatformController {
    @Autowired
    private InvoicePlatformService service;

    @GetMapping({"/mg/invoicePlatform/{id}"})
    public Rest<InvoicePlatform> findById(InvoicePlatform e) throws ParameterException {
        ParameterExceptionUtil.verify("invoicePlatform.id", e.getId()).isPositive();
        InvoicePlatform data = service.findById(e);
        return Rest.createSuccess(data);
    }
    
    @GetMapping("/mg/invoicePlatform/count")
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }
    @GetMapping("/mg/invoicePlatform")
    public Rest<List<InvoicePlatform>> find(SearchCondition searchCondition) {
        List<InvoicePlatform> list = service.find(searchCondition);        
        return Rest.createSuccess(list);
    }
    

}
