package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Tutorial;

import java.util.List;

public interface TutorialService {
    int save(Tutorial e);

    boolean delete(Tutorial e);

    boolean update(Tutorial e);

    Tutorial findById(Tutorial e);

    int count(SearchCondition searchCondition);
    
    List<Tutorial> find(SearchCondition searchCondition);
}
