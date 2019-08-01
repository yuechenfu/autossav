package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.PersonGroupDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.PersonGroup;
import com.hiveel.autossav.service.PersonGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonGroupServiceImpl implements PersonGroupService {
    @Autowired
    private PersonGroupDao dao;

    @Override
    public int save(PersonGroup e) {
        e.fillNotRequire();
        e.updateAt();  
        return dao.save(e);
    }

    @Override
    public boolean delete(PersonGroup e) {
        return dao.delete(e) == 1;
    }

    @Override
    public boolean update(PersonGroup e) {
    	e.updateAt();  
        return dao.update(e) == 1;
    }

    @Override
    public PersonGroup findById(PersonGroup e) {
        PersonGroup result = dao.findById(e);
        return result != null ? result : PersonGroup.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<PersonGroup> find(SearchCondition searchCondition) {
    	searchCondition.setDefaultSortBy("updateAt");
        return dao.find(searchCondition);
    }
}
