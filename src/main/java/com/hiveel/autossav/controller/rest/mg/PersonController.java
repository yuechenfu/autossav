package com.hiveel.autossav.controller.rest.mg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hiveel.auth.sdk.model.Account;
import com.hiveel.autossav.model.ProjectRestCode;
import com.hiveel.autossav.model.conf.SendGridEmailTemplate;
import com.hiveel.core.exception.FailException;
import com.hiveel.core.exception.ServiceException;
import com.hiveel.core.log.util.LogUtil;
import com.hiveel.core.util.SendGridEmailUtil;
import com.hiveel.core.util.ThreadUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.service.PersonGroupService;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;

@RestController
public class PersonController {
    @Autowired
    private PersonService service;
    @Autowired
    private PersonGroupService personGroupService;
    @Autowired
    private SendGridEmailUtil emailUtil;

    @PostMapping("/mg/person")
    public Rest<Long> save(@RequestAttribute("loginPerson") Person loginPerson, Person e, Account account) throws ParameterException, FailException, ServiceException {
        ParameterExceptionUtil.verify("person.firstName", e.getFirstName()).isLengthIn(1, 50);
        ParameterExceptionUtil.verify("person.lastName", e.getLastName()).isLengthIn(1, 50);
        ParameterExceptionUtil.verify("person.group", e.getGroup()).isNotNull();
        ParameterExceptionUtil.verify("person.group.id", e.getGroup().getId()).isNaturalNumber();
        ParameterExceptionUtil.verify("person.type", e.getType()).isNotNull();
        ParameterExceptionUtil.verify("account.password", account.getPassword()).isNotEmpty();
        service.checkPersonExist(e);
        checkType(loginPerson, e);
        service.savePersonAndAccount(e, account);
        notifyNewUser(account);
        return Rest.createSuccess(e.getId());
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

    private void checkType(Person loginPerson, Person e) throws ParameterException{
        switch (loginPerson.getType()) {
            case VE:
                ParameterExceptionUtil.verify("person.type", String.valueOf(e.getType())).beContainedIn(Arrays.asList("VE", "DR")); break;
            case AS:
                ParameterExceptionUtil.verify("person.type", String.valueOf(e.getType())).beContainedIn(Arrays.asList("AS", "REPAIR")); break;
        }
    }

    @DeleteMapping("/mg/person/{id}")
    public Rest<Boolean> delete(@RequestAttribute("loginPerson") Person loginPerson, Person e) throws ParameterException, FailException {
        ParameterExceptionUtil.verify("person.id", e.getId()).isPositive();
        service.deletePersonAndAccount(e);
        return Rest.createSuccess(true);
    }


    @PutMapping({"/mg/person/{id}"})
    public Rest<Boolean> update(@RequestAttribute("loginPerson") Person loginPerson, Person e, Account account) throws ParameterException, FailException  {
        ParameterExceptionUtil.verify("person.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("person.firstName | lastName | phone | driverLicense | type | group | email | password ",
                e.getFirstName(), e.getLastName(), e.getPhone(), e.getType() , e.getDriverLicense(), e.getGroup(),e.getEmail(),account.getPassword()).atLeastOne().isNotEmpty();
        if (e.getType() != null) {
            checkType(loginPerson, e);
        }
        service.updatePersonAndAccount(e, account);
        return Rest.createSuccess(true);
    }

    @PutMapping({"/mg/person/{id}/email"})
    public Rest<Boolean> updateEmail(Person e, Account account) throws ParameterException, FailException {
        ParameterExceptionUtil.verify("person.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("person.email", e.getEmail()).isNotEmpty();
        e.setPhone(null);
        service.updatePersonAndAccount(e, account);
        return Rest.createSuccess(true);
    }

    @DeleteMapping({"/mg/person/{id}/email"})
    public Rest<Boolean> deleteEmail(Person e) throws ParameterException, FailException, ServiceException {
        ParameterExceptionUtil.verify("person.id", e.getId()).isPositive();
        Person inDb = service.findById(e);
        checkAccountDeleteable(inDb);
        service.deleteEmailAccount(inDb);
        return Rest.createSuccess(true);
    }

    @PutMapping({"/mg/person/{id}/phone"})
    public Rest<Boolean> updatePhone(Person e, Account account) throws ParameterException, FailException {
        ParameterExceptionUtil.verify("person.id", e.getId()).isPositive();
        ParameterExceptionUtil.verify("person.phone", e.getPhone()).isNotEmpty();
        e.setEmail(null);
        service.updatePersonAndAccount(e, account);
        return Rest.createSuccess(true);
    }

    @DeleteMapping({"/mg/person/{id}/phone"})
    public Rest<Boolean> deletePhone(Person e) throws ParameterException, FailException, ServiceException {
        ParameterExceptionUtil.verify("person.id", e.getId()).isPositive();
        Person inDb = service.findById(e);
        checkAccountDeleteable(inDb);
        service.deletePhoneAccount(inDb);
        return Rest.createSuccess(true);
    }

    @GetMapping({"/mg/person/{id}"})
    public Rest<Person> findById(Person e) throws ParameterException {
        ParameterExceptionUtil.verify("person.id", e.getId()).isPositive();
        Person person = service.findById(e);
        person.setGroup(personGroupService.findById(person.getGroup()));
        return Rest.createSuccess(person);
    }
    
    @PutMapping({"/mg/me"})
    public Rest<Boolean> updateMe(@RequestAttribute("loginPerson") Person loginPerson, Person e) throws ParameterException {
    	e.setId(loginPerson.getId());
        ParameterExceptionUtil.verify("person.firstName | lastName | phone | driverLicense | group | imgsrc ",
                e.getFirstName(), e.getLastName(), e.getPhone(), e.getDriverLicense(), e.getGroup(), e.getImgsrc()).atLeastOne().isNotEmpty();
        boolean success = service.update(e);
        return Rest.createSuccess(success);
    }

    @GetMapping({"/mg/me"})
    public Rest<Person> me(@RequestAttribute("loginPerson") Person e) throws ParameterException {
        Person person = service.findById(e);
        person.setGroup(personGroupService.findById(person.getGroup()));
        return Rest.createSuccess(person);
    }

    @GetMapping({"/mg/person/count"})
    public Rest<Integer> count(SearchCondition searchCondition) {
        int count = service.count(searchCondition);
        return Rest.createSuccess(count);
    }

    @GetMapping({"/mg/person"})
    public Rest<List<Person>> find(SearchCondition searchCondition) {
        List<Person> list = service.find(searchCondition);
        list.stream().forEach(e -> e.setGroup(personGroupService.findById(e.getGroup())));
        return Rest.createSuccess(list);
    }

    private void checkAccountDeleteable(Person person) throws ServiceException {
        String email = person.getEmail();
        String phone = person.getPhone();
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(phone)) {
            throw new ServiceException(ProjectRestCode.ACCOUNT_DEL_FORBIDDEN.getMessage(), ProjectRestCode.ACCOUNT_DEL_FORBIDDEN);
        }
    }

}
