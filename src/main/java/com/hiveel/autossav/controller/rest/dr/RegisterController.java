package com.hiveel.autossav.controller.rest.dr;

import com.hiveel.auth.sdk.model.Account;
import com.hiveel.autossav.model.conf.SendGridEmailTemplate;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.autossav.service.SecurityCodeService;
import com.hiveel.autossav.service.RegisterService;
import com.hiveel.core.debug.DebugSetting;
import com.hiveel.core.exception.FailException;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.ServiceException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.log.util.LogUtil;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.util.SendGridEmailUtil;
import com.hiveel.core.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController(value = "SignUpController")
public class RegisterController {
    @Autowired
    private PersonService personService;
    @Autowired
    private SecurityCodeService securityCodeService;
    @Autowired
    private RegisterService signUpService;
    @Autowired
    private SendGridEmailUtil emailUtil;

    @PostMapping({"/register/code"})
    public Rest<String> sendRegisterCode(Person person) throws ParameterException, FailException, ServiceException {
        ParameterExceptionUtil.verify("person.email | phone ", person.getEmail(), person.getPhone()).atLeastOne().isNotEmpty();
        personService.checkPersonExist(person);
        SecurityCode securityCode = securityCodeService.saveCodeByPerson(person, SecurityCodeType.REGISTER);
        securityCodeService.sendCode(securityCode);
        //todo shaun 要求直接返回，下个客户端版本修正
        String code =  securityCode.getCode();
        return Rest.createSuccess(code);
    }

    @PostMapping({"/register"})
    public Rest<Boolean> registerByCode(Person person, String code, Account account) throws ParameterException, FailException, ServiceException {
        ParameterExceptionUtil.verify("code", code).isLength(4);
        ParameterExceptionUtil.verify("account.password", account.getPassword()).isNotEmpty();
        ParameterExceptionUtil.verify("person.email | phone ", person.getEmail(), person.getPhone()).atLeastOne().isNotEmpty();
        ParameterExceptionUtil.verify("person.firstName", person.getFirstName()).isNotEmpty();
        personService.checkPersonExist(person);
        signUpService.registerByCode(code, person, account);
        account.setUsername(securityCodeService.getName(person));
        notifyNewUser(account);
        return Rest.createSuccess(true);
    }

    private void notifyNewUser(Account account){
        ThreadUtil.run(()->{
            String name = account.getUsername();
            if(name.indexOf("@")!=-1){
                String templateId = SendGridEmailTemplate.REGISTER_NOTIFY();
                String title = "Welcome to autossav";
                String email = account.getUsername();
                String password = account.getPassword();
                Map<String,Object> data = new HashMap<>();
                data.put("email",email);
                data.put("temporayPassword",password);
                try {
                    emailUtil.sendByTemplate(name,title,templateId,data);
                } catch (Exception e) {
                    LogUtil.error("发送邮件失败:"+account,e);
                }
            }
        });
    }

}
