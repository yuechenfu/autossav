package com.hiveel.autossav.rest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hiveel.autossav.controller.rest.mg.InspectionController;
import com.hiveel.autossav.manager.PushManager;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.conf.SendGridEmailTemplate;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.autossav.service.PushRecordService;
import com.hiveel.autossav.service.VehicleService;
import com.hiveel.core.log.util.LogUtil;
import com.hiveel.core.util.SendGridEmailUtil;
import com.hiveel.push.sdk.service.PushService;
import org.apache.commons.lang.StringUtils;
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
public class InspectionTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private PushRecordService pushRecordService;
    @Autowired
    private PersonService personService;
    @Autowired
    private VehicleService vehicleService;

    @Test
    public void saveDelete() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.post("/mg/inspection").header("authorization", "Bearer 12345678")
            .param("vehicle.id", "1").param("driver.id", "1").param("autosave.id", "3").param("address.id", "1")
            .param("date", "2019-03-07T14:00:00").param("odometer", "1232").param("name", "inspection 1")
        ).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Long>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, saveRest.getCode());
        Long id = saveRest.getData();
        Assert.assertEquals(true,id > 0);
        
        result = mvc.perform(MockMvcRequestBuilders.delete("/mg/inspection/"+id).header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> deleteRest = Rest.fromJson(result);
        boolean success = deleteRest.getData();
        Assert.assertEquals(true, success);
    }
    @Test
    public void update() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/inspection/1").header("authorization", "Bearer 12345678")
                .param("name", "inspection 2").param("status", "QUOTED")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(true, rest.getData());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/inspection/1").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Inspection> findRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Inspection>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, findRest.getCode());
        Assert.assertEquals(InspectionStatus.QUOTED, findRest.getData().getStatus());

        // 数据还原
        mvc.perform(MockMvcRequestBuilders.put("/mg/inspection/1").header("authorization", "Bearer 12345678").param("name", "inspection 2")).andReturn().getResponse().getContentAsString();
    }
    @Test
    public void updateStatus() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/inspection/1").header("authorization", "Bearer 12345678")
            .param("status", "COMPLETE")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(true, rest.getData());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/inspection/1").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Inspection> findRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Inspection>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, findRest.getCode());
        Assert.assertEquals(InspectionStatus.COMPLETE, findRest.getData().getStatus());

        //验证已经发送的推送
        Thread.sleep(100l);
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setMinDate(LocalDateTime.now(ZoneId.of("UTC")).minusMinutes(1).toString());
        Person person = new Person.Builder().set("id",1l).build();
        List<PushRecord> list = pushRecordService.findByPerson(searchCondition,new PushRecord.Builder().set("person",person).set("unread",true).build());
        PushRecord pushRecord = list.get(0);
        Map<String,Object> payLoad = pushRecord.getPayLoad();
        Inspection inspection = (Inspection)payLoad.get("inspection");
        Long inspectionId = inspection.getId();
        Long personId = pushRecord.getPerson().getId();
        //发送的推送消息记录和当前的issue匹配
        Assert.assertEquals(findRest.getData().getId(),inspectionId);
        Assert.assertEquals(findRest.getData().getDriver().getId(),personId);

        Person driver = inspection.getDriver();
        Vehicle vehicle = inspection.getVehicle();
        //检查邮件是否发送
        verifySendMail(vehicle,driver);

        // 数据还原
        mvc.perform(MockMvcRequestBuilders.put("/mg/inspection/1").header("authorization", "Bearer 12345678").param("status", "QUOTED")).andReturn().getResponse().getContentAsString();
        list.stream().forEach(e-> pushRecordService.delete(e));
    }

    private void verifySendMail(Vehicle vehicle, Person person) {
        person = personService.findById(person);
        vehicle = vehicleService.findById(vehicle);
        String email = person.getEmail();
        if (StringUtils.isEmpty(email)) {
            return;
        }
        String templateId = SendGridEmailTemplate.INSPECTION_COMPLETE();
        String carName = vehicle.getName();
        String subject = "Inspection has been completed!";
        Map<String, Object> data = new HashMap<>();
        data.put("carName", carName);
        try {
            Mockito.verify(mockEmailUtil).sendByTemplate(email,subject,templateId,data);
        } catch (Exception e) {
            LogUtil.error("send email fail" + email, e);
        }
    }

    @Test
    public void count() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/inspection/count").header("authorization", "Bearer 12345678")
                .param("status", "COMPLETE")
        ).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(1, (int)rest.getData());
    }
    @Test
    public void find() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/inspection").header("authorization", "Bearer 12345678")
                .param("status", "QUOTED")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Inspection>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Inspection>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(1, rest.getData().size());
    }
    @Test
    public void findByIdList() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/inspection").header("authorization", "Bearer 12345678")
                .param("idList", "1,2")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Inspection>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Inspection>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(3, rest.getData().size());
    }
    @Test
    public void countByVehicle() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicle/1/inspection/count").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, (int)rest.getData());
    }
    @Test
    public void findByVehicle() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicle/1/inspection").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Inspection>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Inspection>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
    }    
    @Test
    public void countByDriver() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/driver/1/inspection/count").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, (int)rest.getData());
    }    
    @Test
    public void saveFail() throws Exception {
        // 没有name
        String result = mvc.perform(MockMvcRequestBuilders.post("/mg/inspection").header("authorization", "Bearer 12345678").param("vehicle.id", "1").param("driver.id", "1")
            	.param("date", "2019-03-07T14:00:00").param("odometer", "1232")).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, saveRest.getCode());
    }
    @Test
    public void deleteFail() throws Exception {
    	String result = mvc.perform(MockMvcRequestBuilders.delete("/mg/inspection/0").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }
    @Test
    public void updateFail() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/inspection/0").header("authorization", "Bearer 12345678")
                .param("name", "inspection2")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }

    @Autowired
    private AuthInterceptor authInterceptor;    
    private AuthManager authManager= Mockito.mock(AuthManager.class);
    Account manager = new Account.Builder().set("id", 2L).set("personId","2").build();

    @Autowired
    private PushManager pushManager;
    private PushService mockPushService = Mockito.mock(PushService.class);

    private SendGridEmailUtil mockEmailUtil;
    @Autowired
    private InspectionController inspectionController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        authInterceptor.setAuthManager(authManager);
        mockEmailUtil = Mockito.mock(SendGridEmailUtil.class);
        inspectionController.setEmailUtil(mockEmailUtil);
        Mockito.when(authManager.findAccountByToken(Mockito.anyString())).thenReturn(manager);
        Mockito.when(mockPushService.sendSingle(Mockito.any(),Mockito.any())).thenReturn(Rest.createSuccess(true));
        pushManager.setPushService(mockPushService);
    }

}
