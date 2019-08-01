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

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class OdometerTest {
    @Autowired
    private OdometerService service;
    
    @Autowired
    private InspectionService inspectionService;
    
    @Autowired
    private IssueService issueService;

    @Test
    public void saveDelete() {
        Vehicle vehicle = new Vehicle.Builder().set("id",1l).build();
        Odometer e = new Odometer.Builder().set("relateId", 1L).set("type", OdometerType.INSPECTION).set("vehicle", vehicle)
                .set("mi", 1242).set("date", "2019-02-28T02:20:20").build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }

    @Test
    public void update() {
        Odometer e = new Odometer.Builder().set("id", 1L).set("mi", 333).build();
        e.setType(OdometerType.INSPECTION);
        e.setRelateId(1L);
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        Odometer inDb = service.findById(new Odometer.Builder().set("id", 1L).build());
        Assert.assertEquals(333, inDb.getMi().intValue());

    }
    
    @Test
    public void count() {
        SearchCondition searchCondition = new SearchCondition();        
        int count = service.count(searchCondition);
        Assert.assertEquals(3, count);
    }        

    @Test
    public void findById() {
        Odometer e = new Odometer.Builder().set("id", 1L).build();
        Odometer inDb = service.findById(e);
        Assert.assertEquals(OdometerType.INSPECTION, inDb.getType());
    }

    @Test
    public void find() {
        SearchCondition searchCondition = new SearchCondition();
        List<Odometer> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
    }

    @Test
    public void findByInspection() {
        Inspection inspection = new Inspection.Builder().set("id", 1L).build();
        Odometer data = service.findByInspection(new SearchCondition(), inspection);
        data.setInspection(inspectionService.findById(new Inspection.Builder().set("id", data.getRelateId()).build()));
        Assert.assertEquals(new Long(1), data.getInspection().getId()  );
    }

    @Test
    public void findByIssue() {
        Issue issue = new Issue.Builder().set("id", 1L).build();
        Odometer data = service.findByIssue(new SearchCondition(), issue);
        data.setIssue(issueService.findById(new Issue.Builder().set("id", data.getRelateId()).build()));
        Assert.assertEquals(new Long(1), data.getIssue().getId()  );
    }

    @Test
    public void findByVehicle(){
        Vehicle vehicle = new Vehicle.Builder().set("id",1l).build();
        List<Odometer> list = service.findByVehicle(new SearchCondition(),vehicle);
        Assert.assertEquals(2,list.size());
    }

}
