package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PushRecordTest {
    @Autowired
    private PushRecordService service;


    @Test
    public void saveDelete() {
        Vehicle vehicle = new Vehicle.Builder().set("id", 1l).build();
        Plan plan = new Plan.Builder().set("id", 1l).build();
        Person person = new Person.Builder().set("id", 1l).build();
        Issue issue = new Issue.Builder().set("id", 1l).build();
        PushRecord e = new PushRecord.Builder()
                .set("type", PushRecordType.PLAN_THREE_DAYS).set("unread",true)
                .set("person", person).set("status", PushRecordStatus.SUCCESS).build();
        e.addPayLoad("issue",issue).addPayLoad("plan",plan).addPayLoad("vehicle",vehicle);
        int count = service.save(e);
        PushRecord indb = service.findById(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }

    @Test
    public void update() {
        PushRecord e = new PushRecord.Builder().set("id", 1l).set("status", PushRecordStatus.FAIL).build();
        service.update(e);
        PushRecord inDb = service.findById(new PushRecord.Builder().set("id", 1l).build());
        Assert.assertEquals(PushRecordStatus.FAIL, inDb.getStatus());
    }

    @Test
    public void find() {
        List<PushRecord> list = service.find(new SearchCondition());
        Assert.assertEquals(true, list.size() > 0);
    }


}
