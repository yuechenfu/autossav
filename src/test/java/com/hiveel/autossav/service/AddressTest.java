package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Address;
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
public class AddressTest {
    @Autowired
    private AddressService service;
    @Test
    public void saveDelete() {
        Address e = new Address.Builder().set("name", "serviceTestAddress").set("content", " ").build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }
    @Test
    public void update() {
        Address e = new Address.Builder().set("id", 1L).set("name", "service test address").build();
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        Address inDb = service.findById(new Address.Builder().set("id", 1L).build());
        Assert.assertEquals("service test address", inDb.getName());
    }
    @Test
    public void findById() {
        Address e = new Address.Builder().set("id", 2L).build();
        Address inDb = service.findById(e);
        Assert.assertEquals("location 2", inDb.getName());
    }
    @Test
    public void find() {
        SearchCondition searchCondition = new SearchCondition();
        List<Address> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
    }
}
