package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.PersonGroup;

import java.util.List;

public interface PersonGroupService {
    int save(PersonGroup e);

    boolean delete(PersonGroup e);

    boolean update(PersonGroup e);

    PersonGroup findById(PersonGroup e);

    int count(SearchCondition searchCondition);
    List<PersonGroup> find(SearchCondition searchCondition);


}
