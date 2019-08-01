package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.AddressDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Address;
import com.hiveel.autossav.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressDao dao;

    @Override
    public int save(Address e) {
        e.fillNotRequire();
        e.updateAt();
        return dao.save(e);
    }

    @Override
    public boolean delete(Address e) {
        return dao.delete(e) == 1;
    }

    @Override
    public boolean update(Address e) {
        e.updateAt();
        return dao.update(e) == 1;
    }

    @Override
    public Address findById(Address e) {
        Address result = dao.findById(e);
        return result != null ? result : Address.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<Address> find(SearchCondition searchCondition) {
        searchCondition.setDefaultSortBy("name", true);
        return dao.find(searchCondition);
    }
    
}
