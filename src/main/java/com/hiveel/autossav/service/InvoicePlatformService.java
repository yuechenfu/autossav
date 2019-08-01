package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.InvoicePlatform;

import java.util.List;

public interface InvoicePlatformService {
    int save(InvoicePlatform e);

    boolean delete(InvoicePlatform e);    

    int deleteAll();

    boolean update(InvoicePlatform e);

    InvoicePlatform findById(InvoicePlatform e);

    int count(SearchCondition searchCondition);
    
    List<InvoicePlatform> find(SearchCondition searchCondition);

}
