package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.VehicleGroup;
import com.hiveel.autossav.model.entity.VehicleStatus;

import com.hiveel.autossav.model.entity.VehicleType;
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
public class VehicleTest {
    @Autowired
    private VehicleService service;
    @Test
    public void saveDelete() {
    	VehicleGroup group = new VehicleGroup.Builder().set("id", 1L).set("name", "Group 1").set("content", " ").build();
        Vehicle e = new Vehicle.Builder().set("name", "car1").set("group", group).set("year", "2006").set("make", "toyota").set("model", "prius").set("type", VehicleType.VE)
        		.set("status", VehicleStatus.ACTIVE).set("vin", "12345678901234567").set("plate", "few-243").set("rental", false).build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }
    @Test
    public void update() {
    	VehicleGroup group = new VehicleGroup.Builder().set("id", 1L).set("name", "Group 1").set("content", " ").build();
        Vehicle e = new Vehicle.Builder().set("id", 1L).set("name", "car1").set("group", group)
        		.set("status", VehicleStatus.ACTIVE).set("vin", "12345678901234567").set("plate", "few-243").set("rental", false).build();
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        Vehicle inDb = service.findById(new Vehicle.Builder().set("id", 1L).build());
        Assert.assertEquals("car1", inDb.getName());

    }
    @Test
    public void findById() {
        Vehicle e = new Vehicle.Builder().set("id", 1L).build();
        Vehicle inDb = service.findById(e);
        Assert.assertEquals("car1", inDb.getName());
    }
    @Test
    public void count() {
        SearchCondition searchCondition = new SearchCondition();
        int count = service.count(searchCondition);
        Assert.assertEquals(2, count);
    }
    @Test
    public void find() {
        SearchCondition searchCondition = new SearchCondition();
        List<Vehicle> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
    }
    @Test
    public void findByGroup() {
        VehicleGroup e = new VehicleGroup.Builder().set("id", 1L).build();
        SearchCondition searchCondition = new SearchCondition();
        List<Vehicle> list = service.findByGroup(searchCondition, e);
        Assert.assertEquals(list.size(), 2);
    }
}
