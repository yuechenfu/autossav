package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Address;

import java.util.List;

public interface AddressService {
    int save(Address e);

    boolean delete(Address e);

    boolean update(Address e);

    Address findById(Address e);

    int count(SearchCondition searchCondition);
    
    List<Address> find(SearchCondition searchCondition);
}
