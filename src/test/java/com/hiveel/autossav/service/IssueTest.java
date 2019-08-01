package com.hiveel.autossav.service;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Issue;
import com.hiveel.autossav.model.entity.IssueStatus;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.Vehicle;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class IssueTest {
    @Autowired
    private IssueService service;
    @Test
    public void saveDelete() {
        Person driver = new Person.Builder().set("id", 1L).set("name", "Mason").build();
        Vehicle vehicle = new Vehicle.Builder().set("id", 1L).set("name", "F350").build();
        Issue e = new Issue.Builder().set("vehicle", vehicle).set("driver", driver).set("name", "flat tire").set("apptMinDate", "2019-03-05T13:00:01")
        		.set("apptMaxDate", "2019-03-06T13:00:01").set("status", IssueStatus.PENDING).set("odometer", 52368).set("lat", 34.2365).set("lon", 117.2368).build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }
    @Test
    public void findById() {
        Issue e = new Issue.Builder().set("id", 1L).build();
        Issue inDb = service.findById(e);
        String name =inDb.getName();
        Assert.assertEquals("Flat tire", name);
    }
    @Test
    public void update() {
        Person driver = new Person.Builder().set("id", 2L).build();
        Issue e = new Issue.Builder().set("id", 1L).set("driver", driver).build();
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        Issue inDb = service.findById(new Issue.Builder().set("id", 1L).build());
        Assert.assertEquals("Flat tire", inDb.getName());
    }
    @Test
    public void count() {
        SearchCondition searchCondition = new SearchCondition();        
        int count = service.count(searchCondition);
        Assert.assertEquals(3, count);
    }  
    @Test
    public void find() {
        SearchCondition searchCondition = new SearchCondition();
        List<Issue> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
        searchCondition.setName("1");;
        list = service.find(searchCondition);
        Assert.assertEquals(1, list.size());
   }
    @Test
    public void countByIdList() {
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setIdList(Arrays.asList("1","2"));
        int count = service.count(searchCondition);
        Assert.assertEquals(3, count);
    }  
    @Test
    public void countByVehicle() {
    	Vehicle vehicle = new Vehicle.Builder().set("id", 2L).build();
        SearchCondition searchCondition = new SearchCondition();        
        int count = service.countByVehicle(searchCondition, vehicle);
        Assert.assertEquals(2, count);
    }  
    @Test
    public void findByVehicle() {
        Vehicle vehicle = new Vehicle.Builder().set("id", 2L).build();
        SearchCondition searchCondition = new SearchCondition();
        List<Issue> list = service.findByVehicle(searchCondition, vehicle);
        Assert.assertEquals(2, list.size());
    }
    @Test
    public void countByDriver() {
    	Person driver = new Person.Builder().set("id", 2L).build(); 
        SearchCondition searchCondition = new SearchCondition();        
        int count = service.countByDriver(searchCondition, driver);
        Assert.assertEquals(2, count);
    } 
    @Test
    public void findByDriver() {
        Person driver = new Person.Builder().set("id", 2L).build();  
        SearchCondition searchCondition = new SearchCondition();
        List<Issue> list = service.findByDriver(searchCondition, driver);
        Assert.assertEquals(2, list.size());
    }
}
