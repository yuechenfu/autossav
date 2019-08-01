package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.VehicleGroup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")

public class VehicleGroupTest {
    @Autowired
    private VehicleGroupService service;
    @Test
    public void saveDelete() {
        VehicleGroup e = new VehicleGroup.Builder().set("name", "Group 1").set("content", " ").build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }
    @Test
    public void update() {
        VehicleGroup e = new VehicleGroup.Builder().set("id", 1L).set("name", "Group 1 updated").build();
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        VehicleGroup inDb = service.findById(new VehicleGroup.Builder().set("id", 1L).build());
        Assert.assertEquals("Group 1 updated", inDb.getName());
    }
    @Test
    public void findById() {
        VehicleGroup e = new VehicleGroup.Builder().set("id", 1L).build();
        VehicleGroup inDb = service.findById(e);
        Assert.assertEquals("Group 1 updated", inDb.getName());
    }
    @Test
    public void find() {
        SearchCondition searchCondition = new SearchCondition();
        List<VehicleGroup> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
    }
    @Test
    public void count() {
    	SearchCondition searchCondition = new SearchCondition();
        int inDb = service.count(searchCondition);
        Assert.assertEquals(3, inDb);
    }
}
