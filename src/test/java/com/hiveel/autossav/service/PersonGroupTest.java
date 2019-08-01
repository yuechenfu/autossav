package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.PersonGroupAuth;
import com.hiveel.autossav.model.entity.PersonGroupType;
import com.hiveel.autossav.model.entity.PersonGroup;
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
public class PersonGroupTest {
    @Autowired
    private PersonGroupService service;
    @Test
    public void saveDelete() {    	
        PersonGroup e = new PersonGroup.Builder().set("type", PersonGroupType.AS).set("name", "serviceTestPersonGroup").set("dashboard", PersonGroupAuth.VIEW).build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }
    @Test
    public void findById() {
        PersonGroup e = new PersonGroup.Builder().set("id", 2L).build();
        PersonGroup inDb = service.findById(e);
        Assert.assertEquals(PersonGroupAuth.EDIT, inDb.getDashboard());
    }
    @Test
    public void update() {
        PersonGroup e = new PersonGroup.Builder().set("id", 2L).set("name", "personGroup test updated").build();
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        PersonGroup inDb = service.findById(new PersonGroup.Builder().set("id", 2L).build());
        Assert.assertEquals("personGroup test updated", inDb.getName());
        Assert.assertEquals(PersonGroupAuth.EDIT, inDb.getDashboard());
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
        List<PersonGroup> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
    }
}
