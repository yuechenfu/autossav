package com.hiveel.autossav.rest;

import com.google.gson.reflect.TypeToken;
import com.hiveel.auth.sdk.model.Account;
import com.hiveel.autossav.controller.interceptor.AuthInterceptor;
import com.hiveel.autossav.dao.PersonDao;
import com.hiveel.autossav.manager.AuthManager;
import com.hiveel.autossav.model.ProjectRestCode;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.conf.SendGridEmailTemplate;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.impl.PersonServiceImpl;
import com.hiveel.autossav.service.impl.SecurityCodeServiceImpl;
import com.hiveel.core.debug.DebugSetting;
import com.hiveel.core.log.util.LogUtil;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.util.SendGridEmailUtil;
import com.hiveel.core.util.ShortMessageUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountTest {

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private SecurityCodeServiceImpl securityCodeService;

    @Test
    public void registerProcessByEmail() throws Exception {
        //注册之前发送安全码
        String email = "helloXXX@hiveel.com";
        String firstName = "hellow";
        String result = mvc.perform(MockMvcRequestBuilders.post("/register/code").header("authorization", "Bearer 12345678")
                .param("email", email)
        ).andReturn().getResponse().getContentAsString();
        Rest<String> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<String>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        String code = rest.getData();

        //验证发送安全码结果
        SecurityCode securityCode = findSecurityCode(code, email, SecurityCodeType.REGISTER);
        Assert.assertEquals(email, securityCode.getName());
        Assert.assertEquals(SecurityCodeStatus.UN_USE, securityCode.getStatus());
        verifySendCodeInvoked(securityCode);



        //使用错误的安全码注册
        String wrongCode = String.valueOf(Long.valueOf(code) + 3).substring(0, 4);
        result = mvc.perform(MockMvcRequestBuilders.post("/register").header("authorization", "Bearer 12345678")
                .param("email", email).param("code", wrongCode).param("password", "12345").param("firstName", firstName)
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> saveRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(ProjectRestCode.SECURITY_CODE_WRONG, saveRest.getCode());

        //虽然注册码正确但是 输入的email是另外一个
        String errEmail = "helloXXXErr@hiveel.com";
        result = mvc.perform(MockMvcRequestBuilders.post("/register").header("authorization", "Bearer 12345678")
                .param("email", errEmail).param("code", code).param("password", "12345").param("firstName", firstName)
        ).andReturn().getResponse().getContentAsString();
        saveRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(ProjectRestCode.SECURITY_CODE_WRONG, saveRest.getCode());

        //使用过期的安全码注册
        LocalDateTime codeCreateAt = securityCode.getCreateAt();
        securityCode.setCreateAt(codeCreateAt.minusMinutes(40));
        updateCodeCreateAt(securityCode);
        result = mvc.perform(MockMvcRequestBuilders.post("/register").header("authorization", "Bearer 12345678")
                .param("email", email).param("code", code).param("password", "12345").param("firstName", firstName)
        ).andReturn().getResponse().getContentAsString();
        saveRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(ProjectRestCode.SECURITY_CODE_EXPIRED, saveRest.getCode());

        //恢复安全码createAt数据
        securityCode.setCreateAt(codeCreateAt);
        updateCodeCreateAt(securityCode);

        //使用该安全码注册
        result = mvc.perform(MockMvcRequestBuilders.post("/register").header("authorization", "Bearer 12345678")
                .param("email", email).param("code", code).param("password", "12345").param("firstName", firstName)
        ).andReturn().getResponse().getContentAsString();
        saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, saveRest.getCode());

        //验证注册结果
        securityCode = findSecurityCode(code, email, SecurityCodeType.REGISTER);
        Assert.assertEquals(SecurityCodeStatus.USED, securityCode.getStatus()); //安全码状态已经变成 "已使用"
        Person personRegistered = findPerson();
        Assert.assertEquals(email, personRegistered.getEmail());
        Assert.assertEquals(PersonType.DR_PRIVATE, personRegistered.getType()); //该接口注册默认的用户为司机
        Assert.assertEquals(firstName,personRegistered.getFirstName());

        //再次使用该邮箱尝试注册 发送安全码
        result = mvc.perform(MockMvcRequestBuilders.post("/register/code").header("authorization", "Bearer 12345678")
                .param("email", email)
        ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<String>>() {}.getType());
        //会返回邮箱已经存在错误码
        Assert.assertEquals(ProjectRestCode.USERNAME_REGISTERED, rest.getCode());

        //再次尝试调用 注册接口
        result = mvc.perform(MockMvcRequestBuilders.post("/register").header("authorization", "Bearer 12345678")
                .param("email", email).param("code", code).param("password", "12345").param("firstName", firstName)
        ).andReturn().getResponse().getContentAsString();
        saveRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        //会返回邮箱已经存在错误码
        Assert.assertEquals(ProjectRestCode.USERNAME_REGISTERED, saveRest.getCode());

        //清理产生的数据
        securityCodeService.delete(securityCode);
        personService.delete(personRegistered);
    }


    @Test
    public void registerProcessByPhone() throws Exception {
        //注册之前发送安全码
        String phone = "19909984888";
        String firstName = "hellow";
        String result = mvc.perform(MockMvcRequestBuilders.post("/register/code").header("authorization", "Bearer 12345678")
                .param("phone", phone)
        ).andReturn().getResponse().getContentAsString();
        Rest<String> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<String>>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        String code = rest.getData();

        //验证发送安全码结果
        SecurityCode securityCode = findSecurityCode(code, phone, SecurityCodeType.REGISTER);
        Assert.assertEquals(phone, securityCode.getName());
        Assert.assertEquals(SecurityCodeStatus.UN_USE, securityCode.getStatus());
        verifySendCodeInvoked(securityCode);

        //使用错误的安全码注册
        String wrongCode = String.valueOf(Long.valueOf(code) + 3).substring(0, 4);
        result = mvc.perform(MockMvcRequestBuilders.post("/register").header("authorization", "Bearer 12345678")
                .param("phone", phone).param("code", wrongCode).param("password", "12345").param("firstName", firstName)
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> saveRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(ProjectRestCode.SECURITY_CODE_WRONG, saveRest.getCode());

        //虽然注册码正确但是 输入的phone是另外一个
        String errPhone = "9998889988";
        result = mvc.perform(MockMvcRequestBuilders.post("/register").header("authorization", "Bearer 12345678")
                .param("phone", errPhone).param("code", code).param("password", "12345").param("firstName", firstName)
        ).andReturn().getResponse().getContentAsString();
        saveRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(ProjectRestCode.SECURITY_CODE_WRONG, saveRest.getCode());

        //使用过期的安全码注册
        LocalDateTime codeCreateAt = securityCode.getCreateAt();
        securityCode.setCreateAt(codeCreateAt.minusMinutes(40));
        updateCodeCreateAt(securityCode);
        result = mvc.perform(MockMvcRequestBuilders.post("/register").header("authorization", "Bearer 12345678")
                .param("phone", phone).param("code", code).param("password", "12345").param("firstName", firstName)
        ).andReturn().getResponse().getContentAsString();
        saveRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(ProjectRestCode.SECURITY_CODE_EXPIRED, saveRest.getCode());

        //恢复安全码createAt数据
        securityCode.setCreateAt(codeCreateAt);
        updateCodeCreateAt(securityCode);

        //使用该安全码注册
        result = mvc.perform(MockMvcRequestBuilders.post("/register").header("authorization", "Bearer 12345678")
                .param("phone", phone).param("code", code).param("password", "12345").param("firstName", firstName)
        ).andReturn().getResponse().getContentAsString();
        saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, saveRest.getCode());

        //验证注册结果
        securityCode = findSecurityCode(code, phone, SecurityCodeType.REGISTER);
        Assert.assertEquals(SecurityCodeStatus.USED, securityCode.getStatus()); //安全码状态已经变成 "已使用"
        Person personRegistered = findPerson();
        Assert.assertEquals(phone, personRegistered.getPhone());
        Assert.assertEquals(PersonType.DR_PRIVATE, personRegistered.getType()); //该接口注册默认的用户为司机
        Assert.assertEquals(firstName, personRegistered.getFirstName());

        //再次使用该邮箱尝试注册 发送安全码
        result = mvc.perform(MockMvcRequestBuilders.post("/register/code").header("authorization", "Bearer 12345678")
                .param("phone", phone)
        ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<String>>() {}.getType());
        //会返回邮箱已经存在错误码
        Assert.assertEquals(ProjectRestCode.USERNAME_REGISTERED, rest.getCode());

        //再次尝试调用 注册接口
        result = mvc.perform(MockMvcRequestBuilders.post("/register").header("authorization", "Bearer 12345678")
                .param("phone", phone).param("code", code).param("password", "12345").param("firstName", firstName)
        ).andReturn().getResponse().getContentAsString();
        saveRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        //会返回邮箱已经存在错误码
        Assert.assertEquals(ProjectRestCode.USERNAME_REGISTERED, saveRest.getCode());

        //清理产生的数据
        securityCodeService.delete(securityCode);
        personService.delete(personRegistered);
    }

    @Test
    public void updateAccoutEmailProcess() throws Exception {
        //数据准备 先有一个用户
        String email = "helloBB@hiveel.com";
        PersonGroup personGroup = new PersonGroup.Builder().set("id", 3L).set("name", "CarManager").build();
        Person person = new Person.Builder().set("group", personGroup).set("firstName", "User").set("lastName", "D").set("phone", "1233232342")
                .set("email", email).set("driverLicense", "13dsfr422e")
                .set("type", PersonType.DR_PRIVATE).set("imgsrc", "").build();
        personService.save(person);
        Account driver = new Account.Builder().set("id", person.getId()).set("personId", person.getId().toString()).set("extra", person.getType().toString()).build();
        Mockito.when(mockAuthManager.findAccountByToken(Mockito.anyString())).thenReturn(driver);

        //更新之前发送安全码
        String newEmail = "helloHAHA@hiveel.com";
        String result = mvc.perform(MockMvcRequestBuilders.post("/dr/me/email/code").header("authorization", "Bearer 12345678")
                .param("email", newEmail)
        ).andReturn().getResponse().getContentAsString();
        Rest<String> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<String>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        String code = rest.getData();

        //验证发送安全码结果
        SecurityCode securityCode = findSecurityCode(code, newEmail, SecurityCodeType.RESET_EMAIL);
        Assert.assertEquals(newEmail, securityCode.getName());
        Assert.assertEquals(SecurityCodeStatus.UN_USE, securityCode.getStatus());
        verifySendCodeInvoked(securityCode);

        //使用错误的安全码更新email
        String wrongCode = String.valueOf(Long.valueOf(code) + 3).substring(0, 4);
        result = mvc.perform(MockMvcRequestBuilders.post("/dr/me/email").header("authorization", "Bearer 12345678")
                .param("email", newEmail).param("code", wrongCode).param("password", "12345")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> updateRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(ProjectRestCode.SECURITY_CODE_WRONG, updateRest.getCode());

        //虽然注册码正确但是 输入的email是另外一个
        String errEmail = "helloXXXErr@hiveel.com";
        result = mvc.perform(MockMvcRequestBuilders.post("/dr/me/email").header("authorization", "Bearer 12345678")
                .param("email", errEmail).param("code", code).param("password", "12345")
        ).andReturn().getResponse().getContentAsString();
        updateRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(ProjectRestCode.SECURITY_CODE_WRONG, updateRest.getCode());

        //使用过期的安全码更新email
        LocalDateTime codeCreateAt = securityCode.getCreateAt();
        securityCode.setCreateAt(codeCreateAt.minusMinutes(40));
        updateCodeCreateAt(securityCode);
        result = mvc.perform(MockMvcRequestBuilders.post("/dr/me/email").header("authorization", "Bearer 12345678")
                .param("email", newEmail).param("code", code)
        ).andReturn().getResponse().getContentAsString();
        updateRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(ProjectRestCode.SECURITY_CODE_EXPIRED, updateRest.getCode());

        //恢复安全码createAt数据
        securityCode.setCreateAt(codeCreateAt);
        updateCodeCreateAt(securityCode);

        //使用该安全码更新email
        result = mvc.perform(MockMvcRequestBuilders.post("/dr/me/email").header("authorization", "Bearer 12345678")
                .param("email", newEmail).param("code", code).param("password", "12345")
        ).andReturn().getResponse().getContentAsString();
        updateRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, updateRest.getCode());

        //验证更新结果
        securityCode = findSecurityCode(code, newEmail, SecurityCodeType.RESET_EMAIL);
        Assert.assertEquals(SecurityCodeStatus.USED, securityCode.getStatus()); //安全码状态已经变成 "已使用"
        Person personUpdated = personService.findById(person);
        Assert.assertEquals(newEmail, personUpdated.getEmail()); //email已更新
        Assert.assertEquals(PersonType.DR_PRIVATE, personUpdated.getType());

        //清理产生的数据
        securityCodeService.delete(securityCode);
        personService.delete(person);
    }

    @Test
    public void updateAccoutPhoneProcess() throws Exception {
        //数据准备 先有一个用户
        PersonGroup personGroup = new PersonGroup.Builder().set("id", 3L).set("name", "CarManager").build();
        Person person = new Person.Builder().set("group", personGroup).set("firstName", "User").set("lastName", "D").set("phone", "1233232342")
                .set("email", "hellow123@hiveel.com").set("driverLicense", "13dsfr422e")
                .set("type", PersonType.DR_PRIVATE).set("imgsrc", "").build();
        personService.save(person);
        Account driver = new Account.Builder().set("id", person.getId()).set("personId", person.getId().toString()).set("extra", person.getType().toString()).build();
        Mockito.when(mockAuthManager.findAccountByToken(Mockito.anyString())).thenReturn(driver);

        //更新之前发送安全码
        String phone = "19909978886";
        String result = mvc.perform(MockMvcRequestBuilders.post("/dr/me/phone/code").header("authorization", "Bearer 12345678")
                .param("phone", phone)
        ).andReturn().getResponse().getContentAsString();
        Rest<String> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<String>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        String code = rest.getData();

        //验证发送安全码结果
        SecurityCode securityCode = findSecurityCode(code, phone, SecurityCodeType.RESET_PHONE);
        Assert.assertEquals(phone, securityCode.getName());
        Assert.assertEquals(SecurityCodeStatus.UN_USE, securityCode.getStatus());
        verifySendCodeInvoked(securityCode);

        //使用错误的安全码更新phone
        String wrongCode = String.valueOf(Long.valueOf(code) + 3).substring(0, 4);
        result = mvc.perform(MockMvcRequestBuilders.post("/dr/me/phone").header("authorization", "Bearer 12345678")
                .param("phone", phone).param("code", wrongCode).param("password", "12345")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> updateRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(ProjectRestCode.SECURITY_CODE_WRONG, updateRest.getCode());

        //虽然注册码正确但是 输入的phone是另外一个
        String errPhone = "9930554678";
        result = mvc.perform(MockMvcRequestBuilders.post("/dr/me/phone").header("authorization", "Bearer 12345678")
                .param("phone", errPhone).param("code", code)
        ).andReturn().getResponse().getContentAsString();
        updateRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(ProjectRestCode.SECURITY_CODE_WRONG, updateRest.getCode());

        //使用过期的安全码更新phone
        LocalDateTime codeCreateAt = securityCode.getCreateAt();
        securityCode.setCreateAt(codeCreateAt.minusMinutes(40));
        updateCodeCreateAt(securityCode);
        result = mvc.perform(MockMvcRequestBuilders.post("/dr/me/phone").header("authorization", "Bearer 12345678")
                .param("phone", phone).param("code", code)
        ).andReturn().getResponse().getContentAsString();
        updateRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(ProjectRestCode.SECURITY_CODE_EXPIRED, updateRest.getCode());

        //恢复安全码createAt数据
        securityCode.setCreateAt(codeCreateAt);
        updateCodeCreateAt(securityCode);

        //使用该安全码注册
        result = mvc.perform(MockMvcRequestBuilders.post("/dr/me/phone").header("authorization", "Bearer 12345678")
                .param("phone", phone).param("code", code).param("password", "12345")
        ).andReturn().getResponse().getContentAsString();
        updateRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, updateRest.getCode());

        //验证更新结果
        securityCode = findSecurityCode(code, phone, SecurityCodeType.RESET_PHONE);
        Assert.assertEquals(SecurityCodeStatus.USED, securityCode.getStatus()); //安全码状态已经变成 "已使用"
        Person personUpdated = personService.findById(person);
        Assert.assertEquals(phone, personUpdated.getPhone()); //phone已更新
        Assert.assertEquals(PersonType.DR_PRIVATE, personUpdated.getType());

        //清理产生的数据
        securityCodeService.delete(securityCode);
        personService.delete(person);
    }

    @Test
    public void deleteAccoutEmailProcess() throws Exception {
        //数据准备 先有一个用户
        PersonGroup personGroup = new PersonGroup.Builder().set("id", 3L).set("name", "CarManager").build();
        Person person = new Person.Builder().set("group", personGroup).set("firstName", "User").set("lastName", "D").set("phone", "1233232342")
                .set("email", "hellow123@hiveel.com").set("driverLicense", "13dsfr422e")
                .set("type", PersonType.DR_PRIVATE).set("imgsrc", "").build();
        personService.save(person);
        Account driver = new Account.Builder().set("id", person.getId()).set("personId", person.getId().toString()).set("extra", person.getType().toString()).build();
        Mockito.when(mockAuthManager.findAccountByToken(Mockito.anyString())).thenReturn(driver);
        Mockito.when(mockAuthManager.deleteByPersonId(Mockito.any())).thenReturn(true); //总是假定auth服务那边是删除成功的

        //尝试清除用户的email 并删除email相关的账号
        String result = mvc.perform(MockMvcRequestBuilders.delete("/dr/me/email").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<String>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());

        //验证更新的结果
        Person personUpdated = personService.findById(person);
        Assert.assertEquals(true, StringUtils.isEmpty(personUpdated.getEmail()));

        //当前用户只有手机号信息，这个时候重复请求清除email信息总会直接返回成功
        result = mvc.perform(MockMvcRequestBuilders.delete("/dr/me/email").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<String>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());

        //当前用户只有手机号信息，这个时候清除手机账号将会失败
        result = mvc.perform(MockMvcRequestBuilders.delete("/dr/me/phone").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<String>>() { }.getType());
        Assert.assertEquals(ProjectRestCode.ACCOUNT_DEL_FORBIDDEN, rest.getCode());

        //清理产生的数据
        personService.delete(person);
    }

    @Test
    public void deleteAccoutPhoneProcess() throws Exception {
        //数据准备 先有一个用户
        PersonGroup personGroup = new PersonGroup.Builder().set("id", 3L).set("name", "CarManager").build();
        Person person = new Person.Builder().set("group", personGroup).set("firstName", "User").set("lastName", "D").set("phone", "1233232342")
                .set("email", "hellow123@hiveel.com").set("driverLicense", "13dsfr422e")
                .set("type", PersonType.DR_PRIVATE).set("imgsrc", "").build();
        personService.save(person);
        Account driver = new Account.Builder().set("id", person.getId()).set("personId", person.getId().toString()).set("extra", person.getType().toString()).build();
        Mockito.when(mockAuthManager.findAccountByToken(Mockito.anyString())).thenReturn(driver);
        Mockito.when(mockAuthManager.deleteByPersonId(Mockito.any())).thenReturn(true); //总是假定auth服务那边是删除成功的

        //尝试清除用户的手机号 并删除手机号相关的账号
        String result = mvc.perform(MockMvcRequestBuilders.delete("/dr/me/phone").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        Rest<Boolean> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<String>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());

        //验证更新的结果
        Person personUpdated = personService.findById(person);
        Assert.assertEquals(true, StringUtils.isEmpty(personUpdated.getPhone()));

        //当前用户只有email信息，这个时候重复请求清除手机信息总会直接返回成功
        result = mvc.perform(MockMvcRequestBuilders.delete("/dr/me/phone").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<String>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());

        //当前用户只有email信息，这个时候清除email账号将会失败
        result = mvc.perform(MockMvcRequestBuilders.delete("/dr/me/email").header("authorization", "Bearer 12345678")
        ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<String>>() { }.getType());
        Assert.assertEquals(ProjectRestCode.ACCOUNT_DEL_FORBIDDEN, rest.getCode());

        //清理产生的数据
        personService.delete(person);
    }


    private SecurityCode findSecurityCode(String code, String name, SecurityCodeType type) {
        //验证安全码
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setType(String.valueOf(type));
        searchCondition.setName(name);
        List<SecurityCode> list = securityCodeService.find(searchCondition);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Autowired
    private PersonDao personDao;

    private Person findPerson() {
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setDefaultSortBy("id");
        searchCondition.setLimit(1);
        List<Person> list = personDao.find(searchCondition);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }



    //验证码 发送短信 或 邮件 远程方法是否已被调用过
    private void verifySendCodeInvoked(SecurityCode e) throws Exception{
        String name = e.getName();
        try {
            if(name.indexOf("@")!=-1){
                String templateId = null;
                String subject = null;
                if (e.getType() == SecurityCodeType.REGISTER) {
                    subject = "Complete sign up";
                    templateId = SendGridEmailTemplate.REGISTER_EMAIL();
                } else {
                    subject = "Change Your Email";
                    templateId = SendGridEmailTemplate.VERIFY_EMAIL();
                }
                Map<String, Object> data = new HashMap<>();
                char[] codes = e.getCode().toCharArray();
                for (int i = 0; i < codes.length; i++) {
                    String key = "code" + (i + 1);
                    data.put(key, new String(new char[]{codes[i]}));
                }
                Mockito.verify(mockSendGridEmailUtil).sendByTemplate(name,subject,templateId,data);
            }else {
                String content = SecurityCodeType.getContent(e);
                Mockito.verify(mockShortMessageUtil).sendByNexmo(name,content);
            }
        } catch (Exception ex) {
            LogUtil.error("send code fail:",ex);
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void updateCodeCreateAt(SecurityCode code) {
        jdbcTemplate.update(" update securityCode set createAt = '" + code.getCreateAt() + "' where id = " + code.getId());
    }

    @Autowired
    private AuthInterceptor authInterceptor;
    @Autowired
    private PersonServiceImpl personService;

    SendGridEmailUtil  mockSendGridEmailUtil = Mockito.mock(SendGridEmailUtil.class);
    ShortMessageUtil mockShortMessageUtil =  Mockito.mock(ShortMessageUtil.class);

    private AuthManager mockAuthManager = Mockito.mock(AuthManager.class);
    Account driver = new Account.Builder().set("id", 1L).set("personId", "1").build();

    boolean debug = DebugSetting.debug;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockSendGridEmailUtil = Mockito.mock(SendGridEmailUtil.class);
        mockShortMessageUtil = Mockito.mock(ShortMessageUtil.class);
        authInterceptor.setAuthManager(mockAuthManager);
        personService.setAuthManager(mockAuthManager);
        securityCodeService.setSendGridEmailUtil(mockSendGridEmailUtil);
        securityCodeService.setShortMessageUtil(mockShortMessageUtil);
        Mockito.when(mockAuthManager.findAccountByToken(Mockito.anyString())).thenReturn(driver);
        Mockito.when(mockAuthManager.checkByUsername(Mockito.any())).thenReturn(false);
        Mockito.when(mockAuthManager.updateByPersonId(Mockito.any())).thenReturn(true);

        DebugSetting.debug = true;
    }

    @After
    public void after() {
        DebugSetting.debug = debug;
    }
}
