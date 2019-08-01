package com.hiveel.autossav.service.impl;

import com.hiveel.autossav.dao.VehicleGroupDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.VehicleGroup;
import com.hiveel.autossav.service.VehicleGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleGroupServiceImpl implements VehicleGroupService {
    @Autowired
    private VehicleGroupDao dao;

    @Override
    public int save(VehicleGroup e) {
        e.fillNotRequire();
        e.updateAt();
        return dao.save(e);
    }

    @Override
    public boolean delete(VehicleGroup e) {
        return dao.delete(e) == 1;
    }

    @Override
    public boolean update(VehicleGroup e) {
    	e.updateAt();
        return dao.update(e) == 1;
    }

    @Override
    public VehicleGroup findById(VehicleGroup e) {
        if (e.getId() != null && e.getId().longValue() == 0l) {
            return new VehicleGroup.Builder().set("name", "DR_PRIVATE").set("id", 0l).set("content", "DR_PRIVATE").build();
        }
        VehicleGroup result = dao.findById(e);
        return result != null ? result : VehicleGroup.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<VehicleGroup> find(SearchCondition searchCondition) {
    	searchCondition.setDefaultSortBy("updateAt");
        return dao.find(searchCondition);
    }
}
