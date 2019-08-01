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
import com.hiveel.autossav.model.entity.PersonGroup;
import com.hiveel.autossav.model.entity.PersonGroupType;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PersonGroupTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Test
    public void saveDelete() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.post("/mg/personGroup").header("authorization", "Bearer 12345678")
            .param("name", "G22").param("type", PersonGroupType.AS.toString())
        ).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Long>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, saveRest.getCode());
        Long id = saveRest.getData();
        Assert.assertEquals(true,id > 0);
        
        result = mvc.perform(MockMvcRequestBuilders.delete("/mg/personGroup/"+id).header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> deleteRest = Rest.fromJson(result);
        boolean success = deleteRest.getData();
        Assert.assertEquals(true, success);
    }
    @Test
    public void update() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/personGroup/2").header("authorization", "Bearer 12345678")
                .param("name", "G23")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(true, rest.getData());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/personGroup/2").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<PersonGroup> findRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<PersonGroup>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, findRest.getCode());
        Assert.assertEquals("G23", findRest.getData().getName());

        // 数据还原
        mvc.perform(MockMvcRequestBuilders.put("/mg/personGroup/2").header("authorization", "Bearer 12345678").param("name", "Driver")).andReturn().getResponse().getContentAsString();
    }
    @Test
    public void count() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/personGroup/count").header("authorization", "Bearer 12345678")
                .param("name", "Driver")
        ).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(1, (int)rest.getData());
    }
    @Test
    public void find() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/personGroup").header("authorization", "Bearer 12345678")
                .param("name", "Driver")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<PersonGroup>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<PersonGroup>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(1, rest.getData().size());
    }

    @Test
    public void saveFail() throws Exception {
        // 字段值超出范围
        String result = mvc.perform(MockMvcRequestBuilders.post("/mg/personGroup").header("authorization", "Bearer 12345678").param("name", "")).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, saveRest.getCode());
    }
    @Test
    public void deleteFail() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.delete("/mg/personGroup/0").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }
    @Test
    public void updateFail() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/personGroup/0").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }
    
    @Autowired
    private AuthInterceptor authInterceptor;    
    private AuthManager authManager= Mockito.mock(AuthManager.class);
    Account manager = new Account.Builder().set("id", 2L).set("personId","2").build();  
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        authInterceptor.setAuthManager(authManager);   
        Mockito.when(authManager.findAccountByToken(Mockito.anyString())).thenReturn(manager);
    }

}
