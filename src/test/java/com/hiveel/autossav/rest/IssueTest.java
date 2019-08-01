package com.hiveel.autossav.rest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hiveel.autossav.controller.rest.mg.IssueController;
import com.hiveel.autossav.manager.PushManager;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.conf.SendGridEmailTemplate;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.IssueService;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.autossav.service.ProblemService;
import com.hiveel.autossav.service.PushRecordService;
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
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.verification.VerificationMode;
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
public class IssueTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private PushRecordService pushRecordService;
    @Autowired
    private PersonService personService;

    @Test
    public void saveDelete() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.post("/mg/issue").header("authorization", "Bearer 12345678")
            .param("vehicle.id", "1").param("driver.id", "1").param("name", "flat tire").param("apptMinDate", "2019-03-05T13:00:01").param("apptMaxDate", "2019-03-06T13:00:01")
            .param("odometer", "52368").param("lat", "34.2365").param("lon", "117.2368")
        ).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Long>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, saveRest.getCode());
        Long id = saveRest.getData();
        Assert.assertEquals(true,id > 0);
        
        result = mvc.perform(MockMvcRequestBuilders.delete("/mg/issue/"+id).header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> deleteRest = Rest.fromJson(result);
        boolean success = deleteRest.getData();
        Assert.assertEquals(true, success);
    }
    @Test
    public void update() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/issue/1").header("authorization", "Bearer 12345678")
                .param("name", "Test Issue Name")
                .param("apptMinDate",LocalDateTime.now(ZoneId.of("UTC")).toString())
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(true, rest.getData());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/issue/1").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Issue> findRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Issue>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, findRest.getCode());
        Assert.assertEquals("Test Issue Name", findRest.getData().getName());

        // 数据还原
        mvc.perform(MockMvcRequestBuilders.put("/mg/issue/1").header("authorization", "Bearer 12345678").param("name", "Flat tire")).andReturn().getResponse().getContentAsString();

    }
    @Test
    public void updateStatus() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/issue/1/status").header("authorization", "Bearer 12345678")
                .param("apptMinDate",LocalDateTime.now(ZoneId.of("UTC")).toString())
                .param("apptMaxDate",LocalDateTime.now(ZoneId.of("UTC")).plusDays(4).toString())
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(true, rest.getData());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/issue/1").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Issue> findRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Issue>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, findRest.getCode());

        //验证已经发送的推送
        Thread.sleep(100l);
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setMinDate(LocalDateTime.now(ZoneId.of("UTC")).minusMinutes(1).toString());
        List<PushRecord> list = pushRecordService.find(searchCondition);
        PushRecord pushRecord = list.get(0);
        Map<String,Object> payLoad = pushRecord.getPayLoad();
        Issue issue = (Issue) payLoad.get("issue");
        Long issueId = issue.getId();
        Long personId = pushRecord.getPerson().getId();
        //发送的推送消息记录和当前的issue匹配
        Assert.assertEquals(findRest.getData().getId(),issueId);
        Assert.assertEquals(findRest.getData().getDriver().getId(),personId);
        Person driver = personService.findById(findRest.getData().getDriver());
        //检查邮件是否真的发送过
        Mockito.verify(mockEmailUtil, new VerificationMode() {
            @Override
            public void verify(VerificationData verificationData) { }
            @Override
            public VerificationMode description(String s) { return null; }
        }).sendByTemplate(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
        // 数据还原
        mvc.perform(MockMvcRequestBuilders.put("/mg/issue/1").header("authorization", "Bearer 12345678").param("status", "QUOTED")).andReturn().getResponse().getContentAsString();
        list.stream().forEach(e-> pushRecordService.delete(e));
    }


    @Test
    public void count() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/issue/count").header("authorization", "Bearer 12345678")
                .param("name", "Flat")
        ).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, (int)rest.getData());
    }
    @Test
    public void countByIdList() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/issue/count").header("authorization", "Bearer 12345678")
                .param("idList", "1,2")
        ).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(3, (int)rest.getData());
    }
    @Test
    public void find() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/issue").header("authorization", "Bearer 12345678")
                .param("name", "Flat")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Issue>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Issue>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
    }
    @Test
    public void countByVehicle() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicle/2/issue/count").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, (int)rest.getData());
    }
    @Test
    public void findByVehicle() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicle/2/issue").header("authorization", "Bearer 12345678")
                .param("name", "Flat")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Issue>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Issue>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
    }
    @Test
    public void countByDriver() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/driver/2/issue/count").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, (int)rest.getData());
    }
    @Test
    public void findByDriver() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/driver/2/issue").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<List<Issue>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Issue>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
    }

    @Test
    public void saveFail() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.post("/mg/issue").param("name", "").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, saveRest.getCode());
    }
    @Test
    public void deleteFail() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.delete("/mg/issue/0").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }
    @Test
    public void updateFail() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/issue/0").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }
    @Test
    public void findByVehicleFail() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicle/0/issue").header("authorization", "Bearer 12345678")
                .param("name", "Flat")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Issue>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Issue>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }

    @Test
    public void findByDriverFail() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/driver/0/issue").header("authorization", "Bearer 12345678")
                .param("name", "Flat")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Issue>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Issue>>>(){}.getType());
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
    private IssueController issueController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        authInterceptor.setAuthManager(authManager);
        mockEmailUtil = Mockito.mock(SendGridEmailUtil.class);
        issueController.setEmailUtil(mockEmailUtil);
        Mockito.when(authManager.findAccountByToken(Mockito.anyString())).thenReturn(manager);
        Mockito.when(mockPushService.sendSingle(Mockito.any(),Mockito.any())).thenReturn(Rest.createSuccess(true));
        pushManager.setPushService(mockPushService);
    }

}
