package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Reminder;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.model.entity.Inspection;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.ReminderType;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ReminderTest {
    @Autowired
    private ReminderService service;
    @Test
    public void saveDelete() {
        Vehicle vehicle = new Vehicle.Builder().set("id", 1L).build();	
        Inspection inspection = new Inspection.Builder().set("id",1L).build();
        Reminder e = new Reminder.Builder().set("vehicle", vehicle).set("inspection",inspection).set("content", "3/23").set("type", ReminderType.INSPECTION_1DAY).set("date", LocalDate.now(ZoneId.of("UTC"))).build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }
    @Test
    public void update() {
        Vehicle vehicle = new Vehicle.Builder().set("id", 1L).build();
        Inspection inspection = new Inspection.Builder().set("id",1L).build();
        Reminder e = new Reminder.Builder().set("id", 1L).set("vehicle", vehicle).set("inspection", inspection).set("type", ReminderType.INSPECTION_1DAY).set("date", LocalDate.now(ZoneId.of("UTC"))).build();      
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        Reminder inDb = service.findById(new Reminder.Builder().set("id", 1L).build());
        Assert.assertEquals(ReminderType.INSPECTION_1DAY, inDb.getType());

    }
    @Test
    public void findById() {
        Reminder e = new Reminder.Builder().set("id", 1L).build();
        Reminder inDb = service.findById(e);
        Assert.assertEquals(1, inDb.getId().intValue());
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
        List<Reminder> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
    }
    @Test
    public void findByVehicle() {
        Vehicle vehicle = new Vehicle.Builder().set("id", 1L).build();
        SearchCondition searchCondition = new SearchCondition();
        List<Reminder> list = service.findByVehicle(searchCondition, vehicle);
        Assert.assertEquals(1, list.size());
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
        List<Reminder> list = service.findByDriver(searchCondition, driver);
        Assert.assertEquals(2, list.size());
    }
}
