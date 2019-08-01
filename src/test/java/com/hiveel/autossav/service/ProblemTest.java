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
public class ProblemTest {
    @Autowired
    private ProblemService service;

    @Test
    public void saveDelete() {
        Exam exam = new Exam.Builder().set("id", 1L).build();
        Vehicle vehicle = new Vehicle.Builder().set("id",1l).build();
        Problem e = new Problem.Builder().set("relateId", 1L).set("type", ProblemType.INSPECTION)
                .set("remark", "hello").set("exam", exam).set("imgsrc1", "imgsrc1").set("imgsrc2", "imgsrc2")
                .set("imgsrc3", "imgsrc3").set("imgsrc4", "imgsrc4").build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }

    @Test
    public void update() {
        String remark = "service test updated";
        Problem e = new Problem.Builder().set("id", 1L).set("remark", remark).build();
        e.setType(ProblemType.INSPECTION);
        e.setRelateId(1L);
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        Problem inDb = service.findById(new Problem.Builder().set("id", 1L).build());
        Assert.assertEquals(remark, inDb.getRemark());

    }
    
    @Test
    public void count() {
        SearchCondition searchCondition = new SearchCondition();        
        int count = service.count(searchCondition);
        Assert.assertEquals(3, count);
    }    
    
    @Test
    public void countByInspection() {
        SearchCondition searchCondition = new SearchCondition(); 
        Inspection inspection = new Inspection.Builder().set("id", 1L).build();
        int count = service.countByInspection(searchCondition, inspection);
        Assert.assertEquals(1, count);
    }  
    
    @Test
    public void countByIssue() {
        SearchCondition searchCondition = new SearchCondition(); 
        Issue issue = new Issue.Builder().set("id", 1L).build();
        int count = service.countByIssue(searchCondition, issue);
        Assert.assertEquals(1, count);
    } 

    @Test
    public void findById() {
        Problem e = new Problem.Builder().set("id", 1L).build();
        Problem inDb = service.findById(e);
        Assert.assertEquals(ProblemType.INSPECTION, inDb.getType());
    }

    @Test
    public void find() {
        SearchCondition searchCondition = new SearchCondition();
        List<Problem> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
    }

    @Test
    public void findByInspection() {
        Inspection inspection = new Inspection.Builder().set("id", 1L).build();
        List<Problem> list = service.findByInspection(new SearchCondition(), inspection);
        Assert.assertEquals(1, list.size());
        Problem problem = list.get(0);
        Assert.assertEquals(new Long(1), problem.getInspection().getId()  );
    }

    @Test
    public void findByIssue() {
        Issue issue = new Issue.Builder().set("id", 1L).build();
        List<Problem> list = service.findByIssue(new SearchCondition(), issue);
        Assert.assertEquals(1, list.size());
        Problem problem = list.get(0);
        Assert.assertEquals(new Long(1), problem.getIssue().getId()  );
    }

    @Test
    public void findByVehicle(){
        Vehicle vehicle = new Vehicle.Builder().set("id",1l).build();
        List<Problem> list = service.findByVehicle(new SearchCondition(),vehicle);
        Assert.assertEquals(2,list.size());
    }

}
