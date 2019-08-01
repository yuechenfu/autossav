package com.hiveel.autossav.service;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.SecurityCode;
import com.hiveel.autossav.model.entity.SecurityCodeStatus;
import com.hiveel.autossav.model.entity.SecurityCodeType;
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
public class SecurityCodeTest {
    @Autowired
    private SecurityCodeService service;
    @Test
    public void curdTest() {
        String name = "xx@zzz.com" ;
        String code = "12345";
        SecurityCodeType type = SecurityCodeType.RESET_EMAIL;
        SecurityCodeStatus status = SecurityCodeStatus.UN_USE;
        SecurityCode e = new SecurityCode.Builder().set("name", name).set("code", code).set("type", type).set("status",status).build();

        //test save
        int count = service.save(e);
        Assert.assertEquals(1, count);
        //test findById
        SecurityCode inDb = service.findById(e);
        Assert.assertEquals(code,inDb.getCode());
        Assert.assertEquals(type,inDb.getType());
        Assert.assertEquals(name,inDb.getName());

        //test find
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setType(String.valueOf(type));
        searchCondition.setStatus(String.valueOf(status));
        searchCondition.setName(name);
        List<SecurityCode> list = service.find(searchCondition);
        Assert.assertEquals(1,list.size());
        //verify find result
        inDb = list.get(0);
        Assert.assertEquals(code,inDb.getCode());
        Assert.assertEquals(type,inDb.getType());
        Assert.assertEquals(name,inDb.getName());
        Assert.assertEquals(status,inDb.getStatus());

        Long id = inDb.getId();
        //test update
        String newCode = "23456";
        SecurityCodeStatus newStatus = SecurityCodeStatus.USED;
        SecurityCode toUpdate = new SecurityCode.Builder().set("id", id).set("code", newCode).set("status",newStatus).build();
        boolean success = service.update(toUpdate);
        Assert.assertEquals(true, success);
        //verify update result
        inDb = service.findById( new SecurityCode.Builder().set("id", id).build());
        Assert.assertEquals(newCode,inDb.getCode());
        Assert.assertEquals(type,inDb.getType());
        Assert.assertEquals(name,inDb.getName());
        Assert.assertEquals(newStatus,inDb.getStatus());

        //test delete
        success = service.delete(new SecurityCode.Builder().set("id", id).build());
        Assert.assertEquals(true, success);

        //test delete result
        inDb = service.findById( new SecurityCode.Builder().set("id", id).build());
        Assert.assertEquals(true,inDb.isNull());
    }

}
