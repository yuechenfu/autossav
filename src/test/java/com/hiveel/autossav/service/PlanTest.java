package com.hiveel.autossav.service;


import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Address;
import com.hiveel.autossav.model.entity.Plan;
import com.hiveel.autossav.model.entity.Vehicle;
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
public class PlanTest {
    @Autowired
    private PlanService service;
   
    @Test
    public void saveDelete() {
        Vehicle vehicle = new Vehicle.Builder().set("id", 1L).build();
        Address address = new Address.Builder().set("id", 1L).build();
        Plan e = new Plan.Builder().set("vehicle", vehicle).set("address", address).set("day", 22).build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }
    @Test
    public void findById() {
        Plan e = new Plan.Builder().set("id", 1L).build();
        Plan inDb = service.findById(e);
        int day =inDb.getDay();
        Assert.assertEquals(11, day);
    }
    @Test
    public void update() {
        Plan e = new Plan.Builder().set("id", 2L).set("day", 12).build();
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        Plan inDb = service.findById(new Plan.Builder().set("id", 2L).build());
        Assert.assertEquals(12, inDb.getDay().intValue());
    }
    @Test
    public void find() {
        SearchCondition searchCondition = new SearchCondition();
        List<Plan> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
    }
    @Test
    public void findByVehicle() {
        Vehicle vehicle = new Vehicle.Builder().set("id", 1L).build();
        SearchCondition searchCondition = new SearchCondition();
        List<Plan> list = service.findByVehicle(searchCondition, vehicle);
        Assert.assertEquals(1, list.size());
    }  
 
}
