package com.hiveel.autossav.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hiveel.autossav.model.entity.PersonType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.PersonGroup;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PersonTest {
    @Autowired
    private PersonService service;
    @Test
    public void saveDelete() throws Exception {
        PersonGroup personGroup = new PersonGroup.Builder().set("id", 3L).set("name", "CarManager").build();
        Person e = new Person.Builder().set("group", personGroup).set("firstName", "User").set("lastName", "D").set("phone", "1233232342")
                .set("email", "user_a@hiveel.com").set("driverLicense", "13dsfr422e").set("username", "test").set("password", "test")
                .set("type", PersonType.VE).set("imgsrc", "").build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        boolean success = service.delete(e);
        Assert.assertEquals(true, success);
    }
    @Test
    public void findById() {
        Person e = new Person.Builder().set("id", 1L).build();
        Person inDb = service.findById(e);
        String firstName =inDb.getFirstName();
        Assert.assertEquals("User", firstName);
    }
    @Test
    public void update() {
        PersonGroup group = new PersonGroup.Builder().set("id", 2L).set("name", "group2").build();
        Person e = new Person.Builder().set("id", 1L).set("group", group).build();
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        Person inDb = service.findById(new Person.Builder().set("id", 1L).build());
        Assert.assertEquals(2, inDb.getGroup().getId().intValue());
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
        List<Person> list = service.find(searchCondition);
        Assert.assertEquals(true, list.size() > 0);
        searchCondition.setName("1");;
        list = service.find(searchCondition);
        Assert.assertEquals(3, list.size());
   }
    @Test
    public void findByTypeList() {
        SearchCondition searchCondition = new SearchCondition(); 
        searchCondition.setTypeList(Arrays.asList("VE", "AS"));
        List<Person> list = service.find(searchCondition);
        Assert.assertEquals(2, list.size());
   }

    @Test
    public void countByEmail() throws Exception {
        String email = "user_abcd@hiveel.com";
        PersonGroup personGroup = new PersonGroup.Builder().set("id", 3L).set("name", "CarManager").build();
        Person e = new Person.Builder().set("group", personGroup).set("firstName", "User").set("lastName", "D").set("phone", "1233232342")
                .set("email", email).set("driverLicense", "13dsfr422e").set("username", "test").set("password", "test")
                .set("type", PersonType.VE).set("imgsrc", "").build();
        service.save(e);
        boolean exist = service.checkEmailExist(email);
        Assert.assertEquals(true, exist);
        //clear data
        service.delete(e);
    }

    @Test
    public void countByPhone() throws Exception {
        String phone = "9099979987";
        PersonGroup personGroup = new PersonGroup.Builder().set("id", 3L).set("name", "CarManager").build();
        Person e = new Person.Builder().set("group", personGroup).set("firstName", "User").set("lastName", "D").set("phone", phone)
                .set("email", "user_abcd@hiveel.com").set("driverLicense", "13dsfr422e").set("username", "test").set("password", "test")
                .set("type", PersonType.VE).set("imgsrc", "").build();
        service.save(e);
        boolean exist = service.checkPhoneExist(phone);
        Assert.assertEquals(true, exist);
        //clear data
        service.delete(e);
    }
}
