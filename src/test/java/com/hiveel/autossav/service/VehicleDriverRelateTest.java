package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.VehicleDriverRelate;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.core.util.DateUtil;
import com.hiveel.autossav.model.entity.Person;

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
public class VehicleDriverRelateTest {
    @Autowired
    private VehicleDriverRelateService service;
    @Test
    public void saveDelete() {
        Vehicle vehicle = new Vehicle.Builder().set("id", 1L).build();
        Person driver = new Person.Builder().set("id", 1L).build(); 	
        VehicleDriverRelate e = new VehicleDriverRelate.Builder().set("vehicle", vehicle).set("driver", driver).set("onDate", DateUtil.newUtcTimeInstance()).set("OnOdometer", "123").build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }
    @Test
    public void update() {
        String offDate=DateUtil.newUtcTimeInstance();
        VehicleDriverRelate e = new VehicleDriverRelate.Builder().set("id", 1L).set("offDate", offDate).set("offOdometer", "123").build();        
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        VehicleDriverRelate inDb = service.findById(new VehicleDriverRelate.Builder().set("id", 1L).build());
        Assert.assertEquals(offDate, inDb.getOffDate());

    }
    @Test
    public void findById() {
        VehicleDriverRelate e = new VehicleDriverRelate.Builder().set("id", 1L).build();
        VehicleDriverRelate inDb = service.findById(e);
        Assert.assertEquals("2019-03-04T15:00:00", inDb.getOnDate());
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
        List<VehicleDriverRelate> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
    }
    @Test
    public void findByVehicle() {
    	Vehicle vehicle = new Vehicle.Builder().set("id", 1L).build();
        SearchCondition searchCondition = new SearchCondition();
        List<VehicleDriverRelate> list = service.findByVehicle(searchCondition, vehicle);
        Assert.assertEquals(1, list.size());
    }
    @Test
    public void findByDriver() {
    	Person driver = new Person.Builder().set("id", 1L).build();     
        SearchCondition searchCondition = new SearchCondition();
        List<VehicleDriverRelate> list = service.findByDriver(searchCondition, driver);
        Assert.assertEquals(2, list.size());
    }
}
