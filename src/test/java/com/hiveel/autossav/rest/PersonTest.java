package com.hiveel.autossav.rest;

import java.util.List;

import com.hiveel.autossav.service.impl.PersonServiceImpl;
import com.hiveel.core.exception.FailException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.reflect.TypeToken;
import com.hiveel.auth.sdk.model.Account;
import com.hiveel.autossav.controller.interceptor.AuthInterceptor;
import com.hiveel.autossav.manager.AuthManager;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PersonTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void saveDelete() throws Exception {
        Mockito.when(mockAuthManager.save(Mockito.any())).thenReturn(new Account.Builder().set("id",10L).build());
        String result = mvc.perform(MockMvcRequestBuilders.post("/mg/person").header("authorization", "Bearer 12345678")
                .param("firstName", "User").param("lastName", "A").param("username", "user_a@hiveel.com").param("password", "123456")
                .param("group.id", "0").param("type", "DR").param("imgsrc", "")
        ).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Long>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, saveRest.getCode());
        Long id = saveRest.getData();
        Assert.assertEquals(true, id > 0);

        result = mvc.perform(MockMvcRequestBuilders.delete("/mg/person/" + id).header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> deleteRest = Rest.fromJson(result);
        boolean success = deleteRest.getData();
        Assert.assertEquals(true, success);
    }

    @Test
    public void update() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/person/1").header("authorization", "Bearer 12345678")
                .param("firstName", "Test")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(true, rest.getData());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/person/1").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Person> findRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Person>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, findRest.getCode());
        Assert.assertEquals("Test", findRest.getData().getFirstName());

        // 数据还原
        mvc.perform(MockMvcRequestBuilders.put("/mg/person/1").header("authorization", "Bearer 12345678").param("firstName", "User")).andReturn().getResponse().getContentAsString();
    }

    @Test
    public void count() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/person/count").header("authorization", "Bearer 12345678")
                .param("name", "User")
        ).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(3, (int) rest.getData());
    }

    @Test
    public void find() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/person").header("authorization", "Bearer 12345678")
                .param("name", "User")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Person>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Person>>>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(3, rest.getData().size());
    }
    
    @Test
    public void findByTypeList() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/person").header("authorization", "Bearer 12345678")
                .param("typeList", "AS,DR")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Person>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Person>>>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
    }

    @Test
    public void saveFail() throws Exception {
        Mockito.when(mockAuthManager.save(Mockito.any())).thenReturn(new Account.Builder().set("id",10L).build());
        // 没有vehicle
        String result = mvc.perform(MockMvcRequestBuilders.post("/mg/person").header("authorization", "Bearer 12345678")
                .param("firstName", "")).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, saveRest.getCode());

        //测试调用远程auth服务失败场景
        //调用之前先记录当前数据库有多少条记录
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/person/count").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        int oldCount = rest.getData(); //没调用save接口之前数据库有3条记录

        //模拟调用远程auth服务失败
        Mockito.when(mockAuthManager.save(Mockito.any())).thenThrow(new FailException("参数缺失"));
        result = mvc.perform(MockMvcRequestBuilders.post("/mg/person").header("authorization", "Bearer 12345678")
                .param("firstName", "User").param("lastName", "A").param("username", "user_a@hiveel.com").param("password", "123456")
                .param("group.id", "0").param("type", "DR").param("imgsrc", "")
        ).andReturn().getResponse().getContentAsString();
        saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Long>>() { }.getType());
        Assert.assertEquals(BasicRestCode.FAIL, saveRest.getCode());

        result = mvc.perform(MockMvcRequestBuilders.get("/mg/person/count").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        int newCount = rest.getData();
        //可以观察到上面会有insert sql 语句日志，不过应该成功回滚
        //验证数据库是否成功回滚 调用后还是3条记录
        Assert.assertEquals(oldCount,newCount);
    }

    @Test
    public void deleteFail() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.delete("/mg/person/0").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());

        //测试调用远程auth服务失败场景
        //调用之前先记录当前数据库有多少条记录
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/person/count").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<Integer> countRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, countRest.getCode());
        int oldCount = countRest.getData(); //没调用save接口之前数据库有3条记录

        String id = String.valueOf(manager.getId());
        //模拟调用远程auth服务失败 deleteByPersonId 异常
        Mockito.when(mockAuthManager.deleteByPersonId(Mockito.any())).thenThrow(new FailException("参数缺失"));
        result = mvc.perform(MockMvcRequestBuilders.delete("/mg/person/"+ id).header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Long>>() { }.getType());
        Assert.assertEquals(BasicRestCode.FAIL, saveRest.getCode());

        result = mvc.perform(MockMvcRequestBuilders.get("/mg/person/count").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        countRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, countRest.getCode());
        int newCount = countRest.getData();
        //可以观察到上面会有insert sql 语句日志，不过应该成功回滚
        //验证数据库是否成功回滚 调用后还是3条记录
        Assert.assertEquals(oldCount,newCount);
    }

    @Test
    public void updateFail() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/person/0").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }

    @Autowired
    private AuthInterceptor authInterceptor;
    @Autowired
    private PersonServiceImpl personService;
    private AuthManager mockAuthManager = Mockito.mock(AuthManager.class);
    Account manager = new Account.Builder().set("id", 2L).set("personId", "2").build();



    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        authInterceptor.setAuthManager(mockAuthManager);
        personService.setAuthManager(mockAuthManager);
        Mockito.when(mockAuthManager.findAccountByToken(Mockito.anyString())).thenReturn(manager);
    }

}
