package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Exam;
import com.hiveel.autossav.model.entity.ExamType;
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
public class ExamTest {
    @Autowired
    private ExamService service;
    @Test
    public void saveDelete() {
        Exam e = new Exam.Builder().set("name", "serviceTestExam").set("content", " ").set("type", ExamType.INSPECTION).build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }
    @Test
    public void update() {
        Exam e = new Exam.Builder().set("id", 1L).set("name", "service test updated").build();
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        Exam inDb = service.findById(new Exam.Builder().set("id", 1L).build());
        Assert.assertEquals("service test updated", inDb.getName());
        Assert.assertEquals(ExamType.INSPECTION, inDb.getType());
    }
    @Test
    public void findById() {
        Exam e = new Exam.Builder().set("id", 1L).build();
        Exam inDb = service.findById(e);
        Assert.assertEquals(ExamType.INSPECTION, inDb.getType());
    }
    @Test
    public void find() {
        SearchCondition searchCondition = new SearchCondition();
        List<Exam> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
    }
}
