package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.TutorialDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Tutorial;
import com.hiveel.autossav.service.TutorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TutorialServiceImpl implements TutorialService {
    @Autowired
    private TutorialDao dao;

    @Override
    public int save(Tutorial e) {
        e.fillNotRequire();
        e.updateAt();
        return dao.save(e);
    }

    @Override
    public boolean delete(Tutorial e) {
        return dao.delete(e) == 1;
    }

    @Override
    public boolean update(Tutorial e) {
        e.updateAt();
        return dao.update(e) == 1;
    }

    @Override
    public Tutorial findById(Tutorial e) {
        Tutorial result = dao.findById(e);
        return result != null ? result : Tutorial.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<Tutorial> find(SearchCondition searchCondition) {
        searchCondition.setDefaultSortBy("name", true);
        return dao.find(searchCondition);
    }
    
}
