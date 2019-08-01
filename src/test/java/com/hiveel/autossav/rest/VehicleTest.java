package com.hiveel.autossav.rest;

import java.util.List;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.OdometerService;
import com.hiveel.autossav.service.VehicleDriverRelateService;
import com.hiveel.autossav.service.VehicleService;
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
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class VehicleTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Test
    public void saveDelete() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.post("/mg/vehicle").header("authorization", "Bearer 123456778")
        	.param("group.id", "1").param("name", "car1").param("status", "ACTIVE").param("vin", "12345678901234567")
            .param("plate", "few-243").param("rental", "0")
        ).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Long>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, saveRest.getCode());
        Long id = saveRest.getData();
        Assert.assertEquals(true,id > 0);
        
        result = mvc.perform(MockMvcRequestBuilders.delete("/mg/vehicle/"+id).header("authorization", "Bearer 123456778")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> deleteRest = Rest.fromJson(result);
        boolean success = deleteRest.getData();
        Assert.assertEquals(true, success);
    }
    @Test
    public void update() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/vehicle/1").header("authorization", "Bearer 123456778")
                .param("name", "car2")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(true, rest.getData());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicle/1").header("authorization", "Bearer 123456778")).andReturn().getResponse().getContentAsString();
        Rest<Vehicle> findRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Vehicle>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, findRest.getCode());
        Assert.assertEquals("car2", findRest.getData().getName());

        // 数据还原
        mvc.perform(MockMvcRequestBuilders.put("/mg/vehicle/1").param("name", "car1").header("authorization", "Bearer 123456778")).andReturn().getResponse().getContentAsString();
    }
    @Test
    public void count() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicle/count").header("authorization", "Bearer 123456778")
                .param("status", "ACTIVE")
        ).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, (int)rest.getData());
    }
    @Test
    public void find() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicle").header("authorization", "Bearer 123456778")
                .param("status", "ACTIVE")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Vehicle>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Vehicle>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
    }
    
    @Test
    public void findByGroup() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicleGroup/1/vehicle").header("authorization", "Bearer 123456778")
                .param("status", "ACTIVE")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Vehicle>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Vehicle>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
    }    

    @Test
    public void saveFail() throws Exception {
        // 没有vehicle
        String result = mvc.perform(MockMvcRequestBuilders.post("/mg/vehicle").param("status", "ACTIVE").header("authorization", "Bearer 123456778")).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, saveRest.getCode());
    }
    @Test
    public void deleteFail() throws Exception {
    	String result = mvc.perform(MockMvcRequestBuilders.delete("/mg/vehicle/0").header("authorization", "Bearer 123456778")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }
    @Test
    public void updateFail() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/vehicle/0").header("authorization", "Bearer 12345678")
                .param("name", "car-no-exist")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }

    @Test
    public void saveByDriver() throws Exception {
        Long personId = 1l;
        Account driver = new Account.Builder().set("id", 2L).set("personId",String.valueOf(personId)).build();
        Mockito.when(authManager.findAccountByToken(Mockito.anyString())).thenReturn(driver);

        String vin = "12345678901234567";
        Integer odmeter = 23000;
        String result = mvc.perform(MockMvcRequestBuilders.post("/dr/vehicle").header("authorization", "Bearer 123456778")
               .param("vin", vin)
                .param("odometer", String.valueOf(odmeter))
        ).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Long>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, saveRest.getCode());
        Long id = saveRest.getData();
        Assert.assertEquals(true,id > 0);
        Thread.sleep(100l);
        //验证保存的车辆
        Vehicle vehicleSaved = vehicleService.findById(new Vehicle.Builder().set("id",id).build());
        Assert.assertEquals(vin,vehicleSaved.getVin());
        Assert.assertEquals(odmeter,vehicleSaved.getOdometer());
        Assert.assertEquals(VehicleStatus.ACTIVE,vehicleSaved.getStatus());

        //验证保存的司机车辆注册信息
        List<VehicleDriverRelate> relateList = vehicleDriverRelateService.find(new SearchCondition());
        VehicleDriverRelate relate = relateList.get(0);
        Assert.assertEquals(personId,relate.getDriver().getId());
        Assert.assertEquals(vehicleSaved.getId(),relate.getVehicle().getId());

        //验证保存的odmeter
        List<Odometer> odometerList = odometerService.find(new SearchCondition());
        Odometer odometer = odometerList.get(0);
        Assert.assertEquals(vehicleSaved.getId(),odometer.getVehicle().getId());
        Assert.assertEquals(odmeter,odometer.getMi());
        Assert.assertEquals(OdometerType.ON_ODOMETER,odometer.getType());

        //清除数据
        vehicleService.delete(vehicleSaved);
        vehicleDriverRelateService.delete(relate);
        odometerService.delete(odometer);
    }

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleDriverRelateService vehicleDriverRelateService;
    @Autowired
    private OdometerService odometerService;

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
