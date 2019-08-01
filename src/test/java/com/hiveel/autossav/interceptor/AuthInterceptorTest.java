package com.hiveel.autossav.interceptor;


import com.hiveel.auth.sdk.model.Account;
import com.hiveel.autossav.controller.interceptor.AuthInterceptor;
import com.hiveel.autossav.manager.AuthManager;
import com.hiveel.core.debug.DebugSetting;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AuthInterceptorTest {

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void asTest() throws Exception {
        String personIdAs = "3";
        Account accountAs = new Account.Builder().set("id", 2L).set("personId", personIdAs).build();
        Mockito.when(mockAuthManager.findAccountByToken(Mockito.anyString())).thenReturn(accountAs);

        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/quote/count")
                .header("authorization", "Bearer 1231241515166")
        ).andReturn().getResponse().getContentAsString();
        Rest getReslut = Rest.fromJson(result, BasicRestCode.class);
        Assert.assertEquals(BasicRestCode.SUCCESS, getReslut.getCode());

        result = mvc.perform(MockMvcRequestBuilders.put("/mg/quote/1")
                .header("authorization", "Bearer 1231241515166").param("name", "Replace item")
        ).andReturn().getResponse().getContentAsString();
        getReslut = Rest.fromJson(result, BasicRestCode.class);
        Assert.assertEquals(BasicRestCode.SUCCESS, getReslut.getCode());

        result = mvc.perform(MockMvcRequestBuilders.put("/mg/issue/1")
                .header("authorization", "Bearer 1231241515166").param("name", "test1 exam 1")
        ).andReturn().getResponse().getContentAsString();
        getReslut = Rest.fromJson(result, BasicRestCode.class);
        Assert.assertEquals(BasicRestCode.SUCCESS, getReslut.getCode());

        result = mvc.perform(MockMvcRequestBuilders.get("/dr/quote/count")
                .header("authorization", "Bearer 1231241515166")
        ).andReturn().getResponse().getContentAsString();
        getReslut = Rest.fromJson(result, BasicRestCode.class);
        Assert.assertEquals(BasicRestCode.UNAUTHORIZED, getReslut.getCode());
    }

    @Test
    public void mgTest() throws Exception {
        String personIdMg = "2";
        Account accountMg = new Account.Builder().set("id", 1L).set("personId", personIdMg).build();
        Mockito.when(mockAuthManager.findAccountByToken(Mockito.anyString())).thenReturn(accountMg);

        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/quote/count")
                .header("authorization", "Bearer 1231241515166")
        ).andReturn().getResponse().getContentAsString();
        Rest getReslut = Rest.fromJson(result, BasicRestCode.class);
        Assert.assertEquals(BasicRestCode.SUCCESS, getReslut.getCode());

        result = mvc.perform(MockMvcRequestBuilders.put("/mg/issue/1")
                .header("authorization", "Bearer 1231241515166").param("name", "test1 exam 1")
        ).andReturn().getResponse().getContentAsString();
        getReslut = Rest.fromJson(result, BasicRestCode.class);
        Assert.assertEquals(BasicRestCode.SUCCESS, getReslut.getCode());

        result = mvc.perform(MockMvcRequestBuilders.put("/mg/quote/1")
                .header("authorization", "Bearer 1231241515166").param("name", "Replace item")
        ).andReturn().getResponse().getContentAsString();
        getReslut = Rest.fromJson(result, BasicRestCode.class);
        Assert.assertEquals(BasicRestCode.SUCCESS, getReslut.getCode());

        result = mvc.perform(MockMvcRequestBuilders.get("/dr/quote/count")
                .header("authorization", "Bearer 1231241515166")
        ).andReturn().getResponse().getContentAsString();
        getReslut = Rest.fromJson(result, BasicRestCode.class);
        Assert.assertEquals(BasicRestCode.UNAUTHORIZED, getReslut.getCode());
    }

    @Test
    public void drTest() throws Exception {
        String personIdDr = "1";
        Account accountDr = new Account.Builder().set("id", 3L).set("personId", personIdDr).build();
        Mockito.when(mockAuthManager.findAccountByToken(Mockito.anyString())).thenReturn(accountDr);

        String result = mvc.perform(MockMvcRequestBuilders.get("/mg/quote/count")
                .header("authorization", "Bearer 1231241515166")
        ).andReturn().getResponse().getContentAsString();
        Rest getReslut = Rest.fromJson(result, BasicRestCode.class);
        Assert.assertEquals(BasicRestCode.UNAUTHORIZED, getReslut.getCode());

        result = mvc.perform(MockMvcRequestBuilders.put("/mg/quote/1")
                .header("authorization", "Bearer 1231241515166").param("name", "Replace item")
        ).andReturn().getResponse().getContentAsString();
        getReslut = Rest.fromJson(result, BasicRestCode.class);
        Assert.assertEquals(BasicRestCode.UNAUTHORIZED, getReslut.getCode());

        result = mvc.perform(MockMvcRequestBuilders.put("/mg/issue/1")
                .header("authorization", "Bearer 1231241515166").param("name", "test1 exam 1")
        ).andReturn().getResponse().getContentAsString();
        getReslut = Rest.fromJson(result, BasicRestCode.class);
        Assert.assertEquals(BasicRestCode.UNAUTHORIZED, getReslut.getCode());

        result = mvc.perform(MockMvcRequestBuilders.get("/dr/quote/count")
                .header("authorization", "Bearer 1231241515166")
        ).andReturn().getResponse().getContentAsString();
        getReslut = Rest.fromJson(result, BasicRestCode.class);
        Assert.assertEquals(BasicRestCode.SUCCESS, getReslut.getCode());
    }

    private AuthManager mockAuthManager = Mockito.mock(AuthManager.class);

    @Autowired
    private AuthInterceptor authInterceptor;

    @Before
    public void setUp() {
        DebugSetting.debug = false;//打开权限拦截器
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        authInterceptor.setAuthManager(mockAuthManager);
    }

    @After
    public void after() {
        DebugSetting.debug = true; ;
    }

}
