package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.InvoicePlatformDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.InvoicePlatform;
import com.hiveel.autossav.service.InvoicePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoicePlatformServiceImpl implements InvoicePlatformService {
    @Autowired
    private InvoicePlatformDao dao;

    @Override
    public int save(InvoicePlatform e) {
        e.updateAt(); 
        return dao.save(e);
    }

    @Override
    public boolean delete(InvoicePlatform e) {
        return dao.delete(e) == 1;
    }

    @Override
    public int deleteAll() {
        return dao.deleteAll();
    }

    @Override
    public boolean update(InvoicePlatform e) {
    	e.updateAt(); 
        return dao.update(e) == 1;
    }

    @Override
    public InvoicePlatform findById(InvoicePlatform e) {
    	InvoicePlatform result = dao.findById(e);
        return result != null ? result : InvoicePlatform.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<InvoicePlatform> find(SearchCondition searchCondition) {
    	searchCondition.setDefaultSortBy("updateAt");
        return dao.find(searchCondition);
    }
}
