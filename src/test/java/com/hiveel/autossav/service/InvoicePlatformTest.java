package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.InvoicePlatform;
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

public class InvoicePlatformTest {
    @Autowired
    private InvoicePlatformService service;
    @Test
    public void saveDelete() {
        InvoicePlatform e = new InvoicePlatform.Builder().set("price", 100.01).set("date", "2019-03-01").build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }
    @Test
    public void update() {
        InvoicePlatform e = new InvoicePlatform.Builder().set("id", 1L).set("price", 100.1).build();
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        InvoicePlatform inDb = service.findById(new InvoicePlatform.Builder().set("id", 1L).build());
        Assert.assertEquals(Double.valueOf(100.1), inDb.getPrice());
    }
    @Test
    public void findById() {
        InvoicePlatform e = new InvoicePlatform.Builder().set("id", 1L).build();
        InvoicePlatform inDb = service.findById(e);
        Assert.assertEquals("2019-03-06T12:32:00", inDb.getDate());
    }
    @Test
    public void find() {
        SearchCondition searchCondition = new SearchCondition();
        List<InvoicePlatform> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
    }
    @Test
    public void count() {
    	SearchCondition searchCondition = new SearchCondition();
        int inDb = service.count(searchCondition);
        Assert.assertEquals(inDb, 1);
    }
}
