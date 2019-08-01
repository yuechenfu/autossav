package com.hiveel.autossav.rest;

import com.google.gson.reflect.TypeToken;
import com.hiveel.auth.sdk.model.Account;
import com.hiveel.autossav.controller.interceptor.AuthInterceptor;
import com.hiveel.autossav.manager.AuthManager;
import com.hiveel.autossav.model.entity.Problem;
import com.hiveel.autossav.model.entity.Quote;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
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

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class QuoteTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Test
    public void saveDelete() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.post("/mg/quote").header("authorization", "Bearer 12345678")
            .param("name", "not auto from controller").param("problem.id", "1").param("labor", "123.45")
        ).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Long>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, saveRest.getCode());
        Long id = saveRest.getData();
        Assert.assertEquals(true,id > 0);
        result = mvc.perform(MockMvcRequestBuilders.delete("/mg/quote/"+id).header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> deleteRest = Rest.fromJson(result);
        boolean success = deleteRest.getData();
        Assert.assertEquals(true, success);
    }
    @Test
    public void saveDeleteFail() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.post("/mg/quote").header("authorization", "Bearer 12345678")
                .param("problem.id", "12345").param("labor", "123.45")
        ).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Long>>() {}.getType());
        Assert.assertEquals(BasicRestCode.PARAMETER, saveRest.getCode());
    }
    @Test
    public void update() throws Exception {
        String updateName = "updated name";
        String id = "1" ;
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/quote/"+id).header("authorization", "Bearer 12345678")
                .param("name", updateName)
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(true, rest.getData());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/quote/"+id).header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Quote> findRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Quote>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(updateName, findRest.getData().getName());
        Assert.assertEquals(true,findRest.getData().getProblem()!=null);
        // 数据还原
        mvc.perform(MockMvcRequestBuilders.put("/mg/quote/"+id).param("name", "Replace item").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
    }
    @Test
    public void updateFail() throws Exception {
        //update 失败 除了id没有任何其他参数
        String id = "1" ;
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/quote/"+id).header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }
    @Test
    public void count() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/quote/count").header("authorization", "Bearer 12345678")
                .param("name", "Replace")
        ).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, (int)rest.getData());
    }
    @Test
    public void find() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/quote").header("authorization", "Bearer 12345678")
                .param("name", "Buy")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Quote>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Quote>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(3, rest.getData().size());
    }
    @Test
    public void findByInspect() throws Exception{
        String id = "1";
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/inspection/"+id+"/quote").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        Rest<List<Quote>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Quote>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/inspection/"+id+"/quote").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Quote>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
    }
    @Test
    public void findByInspectionFail() throws Exception{
        //查询失败，没有输入有效的id
        String id = "0";
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/inspection/"+id+"/quote").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        Rest<List<Quote>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Quote>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }
    @Test
    public void findByIssue() throws Exception{
        String id = "1";
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/issue/"+id+"/quote").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        Rest<List<Quote>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Quote>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/issue/"+id+"/quote").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Quote>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
    }
    @Test
    public void findByIssueFail() throws Exception{
        //查询失败，没有输入有效的id
        String id = "0";
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/issue/"+id+"/quote").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        Rest<List<Quote>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Quote>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }
    @Test
    public void findByProblem() throws Exception{
        String id = "1";
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/problem/"+id+"/quote").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        Rest<List<Quote>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Quote>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/problem/"+id+"/quote").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Quote>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
    }
    @Test
    public void findByVehicle() throws Exception{
        String id = "1";
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicle/"+id+"/quote").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        Rest<List<Problem>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Problem>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(3, rest.getData().size());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicle/"+id+"/quote").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Problem>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(3, rest.getData().size());
    }
    @Test
    public void findByProblemFail() throws Exception{
        //查询失败，没有输入有效的id
        String id = "0";
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/problem/"+id+"/quote").header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<List<Quote>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Quote>>>(){}.getType());
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
