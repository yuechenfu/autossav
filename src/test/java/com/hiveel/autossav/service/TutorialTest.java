package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Tutorial;
import com.hiveel.autossav.model.entity.TutorialType;

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
public class TutorialTest {
    @Autowired
    private TutorialService service;
    @Test
    public void saveDelete() {
        Tutorial e = new Tutorial.Builder().set("name", "tutorial 1").set("filesrc", " ").set("type", TutorialType.VE).build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }
    @Test
    public void update() {
        Tutorial e = new Tutorial.Builder().set("id", 1L).set("name", "tutorial a").build();
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        Tutorial inDb = service.findById(new Tutorial.Builder().set("id", 1L).build());
        Assert.assertEquals("tutorial a", inDb.getName());
    }
    @Test
    public void findById() {
        Tutorial e = new Tutorial.Builder().set("id", 2L).build();
        Tutorial inDb = service.findById(e);
        Assert.assertEquals("tutorial 2", inDb.getName());
    }
    @Test
    public void find() {
        SearchCondition searchCondition = new SearchCondition();
        List<Tutorial> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
    }
}
