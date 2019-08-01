package com.hiveel.autossav.rest;

import com.google.gson.reflect.TypeToken;
import com.hiveel.auth.sdk.model.Account;
import com.hiveel.autossav.controller.interceptor.AuthInterceptor;
import com.hiveel.autossav.manager.AuthManager;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.autossav.service.PushRecordService;
import com.hiveel.autossav.service.VehicleService;
import com.hiveel.core.debug.DebugSetting;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import org.junit.After;
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

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PushRecordTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void countByPerson() throws Exception {
        String id = String.valueOf(personForTest.getId());
        String result = mvc.perform(MockMvcRequestBuilders.get("/dr/me/pushRecord/count").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(count, (int)rest.getData());
    }

    @Test
    public void findByPerson() throws Exception {
        String id = String.valueOf(personForTest.getId());
        String result = mvc.perform(MockMvcRequestBuilders.get("/dr/me/pushRecord").header("authorization", "Bearer 12345678")
                .param("limit","0")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<PushRecord>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<PushRecord>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        List<PushRecord> list = rest.getData();
        Assert.assertEquals(count, list.size());
    }

    @Test
    public void countByUnread() throws Exception {
        String id = String.valueOf(personForTest.getId());
        String result = mvc.perform(MockMvcRequestBuilders.get("/dr/me/pushRecord/count/unread").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        int unreadCount = rest.getData();
        Assert.assertEquals(count / 2, unreadCount);
    }

    @Test
    public void findByUnread() throws Exception {
        String id = String.valueOf(personForTest.getId());
        String result = mvc.perform(MockMvcRequestBuilders.get("/dr/me/pushRecord/unread").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<PushRecord>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<PushRecord>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        List<PushRecord> list = rest.getData();
        Assert.assertEquals(count/2, list.size());
    }


    @Test
    public void updateUnread() throws Exception{
        //找出要更新unread状态的对象
        String id = String.valueOf(personForTest.getId());
        String result = mvc.perform(MockMvcRequestBuilders.get("/dr/me/pushRecord").header("authorization", "Bearer 12345678")
                .param("limit","0")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<PushRecord>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<PushRecord>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        List<PushRecord> list = rest.getData();

        //更新unread
        PushRecord e = list.get(1);
        Assert.assertEquals(true,e.getUnread());
        result = mvc.perform(MockMvcRequestBuilders.put("/dr/me/pushRecord/"+String.valueOf(e.getId())+"/unread").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        //验证更新以后的unread 状态 改成 false
        e = pushRecordService.findById(e);
        Assert.assertEquals(false,e.getUnread());
    }

    @Autowired
    private AuthInterceptor authInterceptor;    
    private AuthManager authManager= Mockito.mock(AuthManager.class);

    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        authInterceptor.setAuthManager(authManager);   
    }

    private boolean oldDebugSetting;

    private Person personForTest;
    private Vehicle vehicleForTest;
    List<PushRecord> testData = new ArrayList<>();
    int count = 20;
    @Autowired
    private PersonService personService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private PushRecordService pushRecordService;

    @Before
    public void prepareTestData() {
        PersonGroup personGroup = new PersonGroup.Builder().set("id", 3L).set("name", "CarManager").build();
        personForTest = new Person.Builder().set("group", personGroup).set("firstName", "User").set("lastName", "D").set("phone", "1233232342")
                .set("email", "user_x@hiveel.com").set("driverLicense", "13dsfr422e").set("type", PersonType.DR).set("imgsrc", "").build();
        personService.save(personForTest);

        Account driver = new Account.Builder().set("id", personForTest.getId()).set("personId", String.valueOf(personForTest.getId())).build();
        Mockito.when(authManager.findAccountByToken(Mockito.anyString())).thenReturn(driver);

        VehicleGroup group = new VehicleGroup.Builder().set("id", 1L).set("name", "Group 1").set("content", " ").build();
        vehicleForTest = new Vehicle.Builder().set("name", "carx").set("group", group)
                .set("status", VehicleStatus.ACTIVE).set("vin", "12345678901234567").set("plate", "few-243").set("rental", false).build();
        vehicleService.save(vehicleForTest);
        for (int i = 0; i < count; i++) {
            PushRecord e = new PushRecord.Builder().set("type", PushRecordType.INSPECTION_COMPLETE).set("person", personForTest).set("status", PushRecordStatus.SUCCESS).build();
            e.addPayLoad("vehicle", vehicleForTest);
            if (i % 2 == 0) {
                e.setUnread(false);
            }
            pushRecordService.save(e);
            testData.add(e);
        }
        oldDebugSetting = DebugSetting.debug;
        DebugSetting.debug = false;
    }

    @After
    public void clearTestData() throws Exception {
        personService.delete(personForTest);
        vehicleService.delete(vehicleForTest);
        testData.stream().forEach(e -> pushRecordService.delete(e));
        DebugSetting.debug = oldDebugSetting;
    }
}
