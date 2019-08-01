package com.hiveel.autossav.service;

import com.hiveel.autossav.model.conf.SendGridEmailTemplate;
import com.hiveel.core.util.SendGridEmailUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * sendGrid 邮件测试，会真的发送邮件 到reciever ，
 * 需要测试时手动把 ignoreTest改成 false  并且修改 reciever的值
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class SendGridEmailTest {

    private String reciever = "cc@gmail.com";

    private boolean ignoreTest = true;

    @Autowired
    private SendGridEmailUtil emailUtil;

    //更新email
    @Test
    public void sendChangeEmailVerify() throws Exception{
        if(ignoreTest) return;
        String subject = "Change Your Email";
        String templateId = SendGridEmailTemplate.VERIFY_EMAIL();
        Map<String, Object> data = new HashMap<>();
        String code = "5678";
        char[] codes = code.toCharArray();
        for (int i = 0; i < codes.length; i++) {
            String key = "code" + (i + 1);
            data.put(key, new String(new char[]{codes[i]}));
        }
        emailUtil.sendByTemplate(reciever, subject, templateId, data);
    }

    //更改密码
    @Test
    public void sendRegisterEmailCode() throws Exception{
        if(ignoreTest) return;
        String subject = "register email";
        String templateId = SendGridEmailTemplate.REGISTER_EMAIL();
        Map<String, Object> data = new HashMap<>();
        String code = "6872";
        char[] codes = code.toCharArray();
        for (int i = 0; i < codes.length; i++) {
            String key = "code" + (i + 1);
            data.put(key, new String(new char[]{codes[i]}));
        }
        emailUtil.sendByTemplate(reciever, subject, templateId, data);
    }

    //注册完成后通知用户
    @Test
    public void sendRegisterNotify() throws Exception{
        if(ignoreTest) return;
        String subject = "Register Notify";
        String templateId = SendGridEmailTemplate.REGISTER_NOTIFY();
        Map<String, Object> data = new HashMap<>();
        String email = "helloNewEmail@hiveel.com";
        String temporayPassword = "412456AC";
        data.put("email",email);
        data.put("temporayPassword",temporayPassword);
        emailUtil.sendByTemplate(reciever, subject, templateId, data);
    }

    //车检之前3天通知
    @Test
    public void sendInspectionIn3Day() throws Exception{
        if(ignoreTest) return;
        String subject = "InspectionIn3Day";
        String templateId = SendGridEmailTemplate.INSPECTION_THREE_DAYS();
        Map<String, Object> data = new HashMap<>();
        data.put("inspectionLocation","1223 Lakes Dr, West Covina,CA 91243");
        data.put("inspectionDate","Jan 1st,2019");
        emailUtil.sendByTemplate(reciever, subject, templateId, data);
    }

    //车检当天通知
    @Test
    public void sendInspectionInOneDay() throws Exception{
        if(ignoreTest) return;
        String subject = "InspectionIn1Day";
        String templateId = SendGridEmailTemplate.INSPECTION_TODAY();
        Map<String, Object> data = new HashMap<>();
        data.put("inspectionLocation","1223 Lakes Dr, West Covina,CA 91243");
        data.put("inspectionDate","Jan 1st,2019");
        emailUtil.sendByTemplate(reciever, subject, templateId, data);
    }

    //车检完成通知
    @Test
    public void sendInspectionComplete() throws Exception{
        if(ignoreTest) return;
        String subject = "InspectionComplete";
        String templateId = SendGridEmailTemplate.INSPECTION_COMPLETE();
        Map<String, Object> data = new HashMap<>();
        data.put("carName","Toyota Lexus ES350");
        emailUtil.sendByTemplate(reciever, subject, templateId, data);
    }

    //报告定价确定通知
    @Test
    public void sendIssueConfirm() throws Exception{
        if(ignoreTest) return;
        String subject = "IssueConfirm";
        String templateId = SendGridEmailTemplate.ISSUE_CONFIRM();
        Map<String, Object> data = new HashMap<>();
        data.put("serviceName","replace tires");
        data.put("serviceTime","Feb 1st,2019 - Feb 6st,2019");
        data.put("note","tires broken has to be replaced");
        emailUtil.sendByTemplate(reciever, subject, templateId, data);
    }
}
