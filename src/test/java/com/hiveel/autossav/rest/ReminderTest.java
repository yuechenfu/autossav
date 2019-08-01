package com.hiveel.autossav.rest;

import java.util.List;

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
import com.hiveel.autossav.model.entity.Reminder;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ReminderTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
        
    @Test
    public void count() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/dr/me/reminder/count").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, (int)rest.getData());
    }
    @Test
    public void find() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/dr/me/reminder").header("authorization", "Bearer 12345678")
                .param("type", "INSPECTION_1DAY")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Reminder>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Reminder>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
    }
    
    @Test
    public void findByVehicle() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/dr/me/vehicle/1/reminder").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Reminder>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Reminder>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(1, rest.getData().size());
    }   
    @Test
    public void countByDriver() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/dr/me/reminder/count").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, (int)rest.getData());
    }

    @Autowired
    private AuthInterceptor authInterceptor;    
    private AuthManager authManager= Mockito.mock(AuthManager.class);
    Account driver = new Account.Builder().set("id", 1L).set("personId","1").build();
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        authInterceptor.setAuthManager(authManager);   
        Mockito.when(authManager.findAccountByToken(Mockito.anyString())).thenReturn(driver);
    }
}
