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
public class QuoteTest {
    @Autowired
    private QuoteService service;

    @Test
    public void saveDelete() {
        Problem problem = new Problem.Builder().set("id",1234l).build();
        Quote e = new Quote.Builder().set("name", "serviceTestQuote").set("problem", problem).set("part", 123.12d).build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }

    @Test
    public void update() {
        Quote e = new Quote.Builder().set("id", 1L).set("name", "service test updated").build();
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        Quote inDb = service.findById(new Quote.Builder().set("id", 1L).build());
        Assert.assertEquals(inDb.getName(), "service test updated");
    }

    @Test
    public void findById() {
        Quote e = new Quote.Builder().set("id", 1L).build();
        Quote inDb = service.findById(e);
        Assert.assertEquals(true, inDb != null);
    }

    @Test
    public void find() {
        SearchCondition searchCondition = new SearchCondition();
        List<Quote> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
    }

    @Test
    public void findByInspection() {
        Inspection inspection = new Inspection.Builder().set("id",1L).build();
        List<Quote> list = service.findByInspection(new SearchCondition(),inspection);
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void findByIssue() {
        Issue issue = new Issue.Builder().set("id",1L).build();
        List<Quote> list = service.findByIssue(new SearchCondition(),issue);
        Assert.assertEquals(2, list.size());
    }        

    @Test
    public void findByProblem() {
        Problem problem = new Problem.Builder().set("id",1L).build();
        List<Quote> list = service.findByProblem(new SearchCondition(),problem);
        Assert.assertEquals(2, list.size());
    }
    
    @Test
    public void deleteByProblem() {
        Problem problem = new Problem.Builder().set("id",1l).build();
        boolean success = service.deleteByProblem(problem);
        Assert.assertEquals(true, success);
    }
}
