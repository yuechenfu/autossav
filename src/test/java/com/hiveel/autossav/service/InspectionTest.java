package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Address;
import com.hiveel.autossav.model.entity.Inspection;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.core.util.DateUtil;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.InspectionStatus;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class InspectionTest {
    @Autowired
    private InspectionService service;
    @Test
    public void saveDelete() {
        Vehicle vehicle = new Vehicle.Builder().set("id", 1L).build();
        Person driver = new Person.Builder().set("id", 1L).build(); 	
        Person autosave = new Person.Builder().set("id", 3L).build(); 
        Address address = new Address.Builder().set("id", 1L).build();
        Inspection e = new Inspection.Builder().set("vehicle", vehicle).set("driver", driver).set("autosave", autosave).set("address", address).set("date", DateUtil.newUtcInstance() ).set("odometer", 1231)
        		.set("name", "inspection week 1").set("content", "").set("status", InspectionStatus.COMPLETE).set("tax", 0.1).build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }
    @Test
    public void update() {
        Vehicle vehicle = new Vehicle.Builder().set("id", 1L).build();
        Person driver = new Person.Builder().set("id", 1L).build(); 
        Inspection e = new Inspection.Builder().set("id", 1L).set("vehicle", vehicle).set("driver", driver).set("date", DateUtil.newUtcInstance() ).set("odometer", 1231)
        		.set("name", "inspection week 2").set("content", "").set("status", InspectionStatus.COMPLETE).set("tax", 0.1).build();        
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        Inspection inDb = service.findById(new Inspection.Builder().set("id", 1L).build());
        Assert.assertEquals("inspection week 2", inDb.getName());

    }
    @Test
    public void findById() {
        Inspection e = new Inspection.Builder().set("id", 1L).build();
        Inspection inDb = service.findById(e);
        Assert.assertEquals("inspection week 2", inDb.getName());
    }
    @Test
    public void find() {
        SearchCondition searchCondition = new SearchCondition();        
        List<Inspection> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
    }
    @Test
    public void findByIdList() {
        SearchCondition searchCondition = new SearchCondition(); 
        searchCondition.setIdList(Arrays.asList("1", "2"));
        List<Inspection> list = service.find(searchCondition);
        Assert.assertEquals(3, list.size());
    }
    @Test
    public void countByVehicle() {
        SearchCondition searchCondition = new SearchCondition();  
        Vehicle vehicle = new Vehicle.Builder().set("id", 1L).build();
        int count = service.countByVehicle(searchCondition, vehicle);
        Assert.assertEquals(2, count);
    } 
    @Test
    public void findByVehicle() {
        Vehicle vehicle = new Vehicle.Builder().set("id", 1L).build();
        SearchCondition searchCondition = new SearchCondition();
        List<Inspection> list = service.findByVehicle(searchCondition, vehicle);
        Assert.assertEquals(2, list.size());
    }
    @Test
    public void countByDriver() {
        SearchCondition searchCondition = new SearchCondition();  
        Person driver = new Person.Builder().set("id", 1L).build();  
        int count = service.countByDriver(searchCondition, driver);
        Assert.assertEquals(2, count);
    } 
    @Test
    public void findByDriver() {
        Person driver = new Person.Builder().set("id", 1L).build();     
        SearchCondition searchCondition = new SearchCondition();
        List<Inspection> list = service.findByDriver(searchCondition, driver);
        Assert.assertEquals(2, list.size());
    }
}
