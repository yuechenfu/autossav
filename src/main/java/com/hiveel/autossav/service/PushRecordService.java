package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.PushRecord;

import java.util.List;

public interface PushRecordService {
    int save(PushRecord e);

    boolean update(PushRecord e);

    boolean delete(PushRecord e);

    PushRecord findById(PushRecord e);

    int count(SearchCondition searchCondition);

    List<PushRecord> find(SearchCondition searchCondition);

    int countByPerson(SearchCondition searchCondition, PushRecord e);

    List<PushRecord> findByPerson(SearchCondition searchCondition, PushRecord e);
}
