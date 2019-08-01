package com.hiveel.autossav.rest;

import com.google.gson.reflect.TypeToken;
import com.hiveel.auth.sdk.model.Account;
import com.hiveel.autossav.controller.interceptor.AuthInterceptor;
import com.hiveel.autossav.manager.AuthManager;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.model.entity.Problem;
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
public class ProblemTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Test
    public void saveDelete() throws Exception {
        //测试插入issue类型
        String relatedIdIssue = "1";
        ProblemType type = ProblemType.ISSUE;
        String examId = "1";
        String result = mvc.perform(MockMvcRequestBuilders.post("/mg/problem").header("authorization", "Bearer 12345678")
                .param("relateId",relatedIdIssue)
                .param("type", type.toString())
                .param("exam.id",examId)
        ).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Long>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, saveRest.getCode());
        Long id = saveRest.getData();
        Assert.assertEquals(true,id > 0);
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/problem/"+id).header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Problem> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Problem>>() {}.getType());
        Problem problem = rest.getData();
        //验证保存后的数值和输入的一致
        Assert.assertEquals(id,problem.getId());
        Assert.assertEquals(type,problem.getType());
        Assert.assertEquals(examId,String.valueOf(problem.getExam().getId()));
        Assert.assertEquals(relatedIdIssue,String.valueOf(problem.getIssue().getId()));
        //清理插入的记录
        result = mvc.perform(MockMvcRequestBuilders.delete("/mg/problem/"+id).header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Boolean> deleteRest = Rest.fromJson(result);
        boolean success = deleteRest.getData();
        Assert.assertEquals(true, success);

        //测试插入inspection类型
        String relatedIdInspect = "1";
        type = ProblemType.INSPECTION;
        examId = "1";
        result = mvc.perform(MockMvcRequestBuilders.post("/mg/problem").header("authorization", "Bearer 12345678")
                .param("relateId", relatedIdInspect)
                .param("type", type.toString())
                .param("exam.id", examId)
        ).andReturn().getResponse().getContentAsString();
        saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Long>>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, saveRest.getCode());
        id = saveRest.getData();
        Assert.assertEquals(true, id > 0);
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/problem/" + id).header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Problem>>() { }.getType());
        problem = rest.getData();
        //验证保存后的数值和输入的一致
        Assert.assertEquals(id, problem.getId());
        Assert.assertEquals(type, problem.getType());
        Assert.assertEquals(examId, String.valueOf(problem.getExam().getId()));
        Assert.assertEquals(relatedIdInspect, String.valueOf(problem.getInspection().getId()));

        //清理插入的记录
        result = mvc.perform(MockMvcRequestBuilders.delete("/mg/problem/" + id).header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        deleteRest = Rest.fromJson(result);
        success = deleteRest.getData();
        Assert.assertEquals(true, success);
    }
    @Test
    public void saveFail() throws Exception {
        String relatedIdIssue = "1";
        String type = "ISSUE";
        String examId = "1";
        //参数缺失 relatedId
        String result = mvc.perform(MockMvcRequestBuilders.post("/mg/problem").header("authorization", "Bearer 12345678")
                .param("type", type)
                .param("exam.id", examId)
        ).andReturn().getResponse().getContentAsString();
        Rest<Long> saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Long>>() { }.getType());
        String msg = saveRest.getMessage();
        Assert.assertEquals(BasicRestCode.PARAMETER, saveRest.getCode());

    }
    @Test
    public void update() throws Exception {
        String id = "1";
        String relatedId = "1";
        ProblemType type = ProblemType.ISSUE;
        String remark = "updated name";
        String oldResult = mvc.perform(MockMvcRequestBuilders.get("/mg/problem/" + id).header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        Rest<Problem> findRest = Rest.fromJson(oldResult, BasicRestCode.class, new TypeToken<Rest<Problem>>() {}.getType());
        Problem problem = findRest.getData();
        String remarkOld = problem.getRemark();
        String relatedIdOld = String.valueOf(problem.getRelateId());
        ProblemType typeOld = problem.getType();
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/problem/" + id).header("authorization", "Bearer 12345678")
                .param("remark", remark)
                .param("type", type.toString())
                .param("relateId", relatedId)
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(true, rest.getData());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/problem/" + id).header("authorization", "Bearer 12345678")).andReturn().getResponse().getContentAsString();
        findRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Problem>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        problem = findRest.getData();
        Assert.assertEquals(true, problem != null);
        //验证保存后的数值和输入的一致
        Assert.assertEquals(id, String.valueOf(problem.getId()));
        Assert.assertEquals(remark, problem.getRemark());
        Assert.assertEquals(type, problem.getType());
        Assert.assertEquals(relatedId, String.valueOf(problem.getIssue().getId()));

        // 数据还原
        result = mvc.perform(MockMvcRequestBuilders.put("/mg/problem/" + id).header("authorization", "Bearer 12345678")
                .param("remark", remarkOld)
                .param("type", typeOld.toString())
                .param("relatedId", relatedIdOld)
        ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
    }
    @Test
    public void updateFail() throws Exception {
        String id = "1";
        //更新参数缺失
        String result = mvc.perform(MockMvcRequestBuilders.put("/mg/problem/" + id).header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result);
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }
    @Test
    public void count() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/problem/count").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(3, (int)rest.getData());
    }
    @Test
    public void countByInspection() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/inspection/1/problem/count").header("authorization", "Bearer 12345678").param("inspection.id", "1")
        ).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(1, (int)rest.getData());
    }
    @Test
    public void countByIssue() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/issue/1/problem/count").header("authorization", "Bearer 12345678").param("issue.id", "1")
        ).andReturn().getResponse().getContentAsString();
        Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(1, (int)rest.getData());
    }
    @Test
    public void find() throws Exception {
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/problem").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<Problem>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Problem>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(3, rest.getData().size());
    }
    @Test
    public void findByInspect() throws Exception{
        String id = "1";
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/inspection/"+id+"/problem").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        Rest<List<Problem>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Problem>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(1, rest.getData().size());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/inspection/"+id+"/problem").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Problem>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(1, rest.getData().size());
    }
    @Test
    public void findByInspectFail() throws Exception{
        //查询失败，没有输入有效的id
        String id = "0";
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/inspection/"+id+"/problem").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        Rest<List<Problem>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Problem>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }
    @Test
    public void findByIssue() throws Exception{
        String id = "1";
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/issue/"+id+"/problem").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        Rest<List<Problem>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Problem>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(1, rest.getData().size());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/issue/"+id+"/problem").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Problem>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(1, rest.getData().size());
    }
    @Test
    public void findByIssueFail() throws Exception{
        //查询失败，没有输入有效的id
        String id = "0";
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/issue/"+id+"/problem").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        Rest<List<Problem>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Problem>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }
    @Test
    public void findByProblemFail() throws Exception{
        //查询失败，没有输入有效的id
        String id = "0";
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicle/"+id+"/problem").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        Rest<List<Problem>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Problem>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.PARAMETER, rest.getCode());
    }
    @Test
    public void findByVehicle() throws Exception{
        String id = "1";
        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicle/"+id+"/problem").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        Rest<List<Problem>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Problem>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/vehicle/"+id+"/problem").header("authorization", "Bearer 12345678")  ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<Problem>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        Assert.assertEquals(2, rest.getData().size());
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
