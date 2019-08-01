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
import com.hiveel.autossav.model.entity.VehicleDriverRelate;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class VehicleDriverRelateTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;   
        
    @Test
    public void save() throws Exception {     
    	Mockito.when(authManager.findAccountByToken(Mockito.anyString())).thenReturn(driver);
        String result = mvc.perform(MockMvcRequestBuilders.post("/dr/me/vehicleDriverRelate").header("authorization", "Bearer 123456778")
            .param("vehicle.id", "1").param("onDate", "2019-03-07T14:00:00").param("OnOdometer", "123")).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Long>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, saveRest.getCode());
        Long id = saveRest.getData();
        Assert.assertEquals(true,id > 0);        
    }
    @Test
    public void update() throws Exception {
    	Mockito.when(authManager.findAccountByToken(Mockito.anyString())).thenReturn(manager);
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/vehicleDriverRelate/1").header("authorization", "Bearer 123456778")
                .param("offDate", "2019-03-10T14:00:00").param("OffOdometer", "123")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(true, rest.getData());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicleDriverRelate/1").header("authorization", "Bearer 123456778")).andReturn().getResponse().getContentAsString();
        Rest<VehicleDriverRelate> findRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<VehicleDriverRelate>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, findRest.getCode());
        Assert.assertEquals("2019-03-10T14:00:00", findRest.getData().getOffDate());

        // 数据还原
        mvc.perform(MockMvcRequestBuilders.put("/mg/vehicleDriverRelate/1").param("offDate", "2019-03-04T23:00:00")).andReturn().getResponse().getContentAsString();
    }
    @Test
    public void count() throws Exception {
    	Mockito.when(authManager.findAccountByToken(Mockito.anyString())).thenReturn(manager);
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicleDriverRelate/count").header("authorization", "Bearer 123456778")).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(3, (int)rest.getData());
    }
    @Test
    public void find() throws Exception {
    	Mockito.when(authManager.findAccountByToken(Mockito.anyString())).thenReturn(manager);
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicleDriverRelate").header("authorization", "Bearer 123456778")
                .param("onDate", "2019-03-04T15:00:00")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<VehicleDriverRelate>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<VehicleDriverRelate>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
    }
    
    @Test
    public void findByVehicle() throws Exception {
    	Mockito.when(authManager.findAccountByToken(Mockito.anyString())).thenReturn(manager);
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicle/1/vehicleDriverRelate").header("authorization", "Bearer 123456778")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<VehicleDriverRelate>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<VehicleDriverRelate>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
    }    

    @Test
    public void saveFail() throws Exception {
        // 没有header
        String result = mvc.perform(MockMvcRequestBuilders.post("/dr/me/vehicleDriverRelate")
                .param("vehicle.id", "1").param("onDate", "2019-03-07T14:00:00").param("OnOdometer", "123")
            ).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.UNAUTHORIZED, saveRest.getCode());
        
        //token not right
        Mockito.when(authManager.findAccountByToken(Mockito.anyString())).thenReturn(Account.NULL);
        result = mvc.perform(MockMvcRequestBuilders.post("/dr/me/vehicleDriverRelate").header("authorization", "Bearer 123456778")
                .param("vehicle.id", "1").param("onDate", "2019-03-07T14:00:00").param("OnOdometer", "123")).andReturn().getResponse().getContentAsString();
        saveRest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.UNAUTHORIZED, saveRest.getCode());
    }
    @Test
    public void updateFail() throws Exception {
    	Mockito.when(authManager.findAccountByToken(Mockito.anyString())).thenReturn(manager);
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/vehicleDriverRelate/1").header("authorization", "Bearer 123456778")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }

    @Autowired
    private AuthInterceptor authInterceptor;
    
    private AuthManager authManager= Mockito.mock(AuthManager.class);
    
    Account driver = new Account.Builder().set("id", 1L).set("personId","1").build();
    Account manager = new Account.Builder().set("id", 2L).set("personId","2").build();  
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        authInterceptor.setAuthManager(authManager);                          
    }
    
}
