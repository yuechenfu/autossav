package com.hiveel.autossav.controller.rest.dr;

import com.hiveel.autossav.model.ProjectRestCode;
import com.hiveel.autossav.model.entity.SecurityCode;
import com.hiveel.autossav.model.entity.SecurityCodeType;
import com.hiveel.autossav.service.SecurityCodeService;
import com.hiveel.core.debug.DebugSetting;
import com.hiveel.core.exception.FailException;
import com.hiveel.core.exception.ServiceException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.service.PersonGroupService;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;

@RestController(value = "DriverPersonController")
public class PersonController {
    @Autowired
    private PersonService service;
    @Autowired
    private PersonGroupService personGroupService;
    @Autowired
    private SecurityCodeService securityCodeService;

    @PutMapping({"/dr/me"})
    public Rest<Boolean> update(@RequestAttribute("loginPerson") Person loginPerson, Person e) throws ParameterException {
        e.setId(loginPerson.getId());
        ParameterExceptionUtil.verify("person.firstName | lastName | phone | driverLicense | group  ",
                e.getFirstName(), e.getLastName(), e.getPhone(), e.getDriverLicense(), e.getGroup()).atLeastOne().isNotEmpty();
        e.setEmail(null);
        e.setPhone(null);
        boolean success = service.update(e);
        return Rest.createSuccess(success);
    }

    @GetMapping({"/dr/me"})
    public Rest<Person> findById(@RequestAttribute("loginPerson") Person loginPerson) throws ParameterException {
        Person person = service.findById(loginPerson);
        person.setGroup(personGroupService.findById(person.getGroup()));
        return Rest.createSuccess(person);
    }

    @PostMapping({"/dr/me/phone/code"})
    public Rest<String> sendUpdatePhoneCode(@RequestAttribute("loginPerson") Person loginPerson, Person e) throws ParameterException, FailException, ServiceException {
        ParameterExceptionUtil.verify("person.phone ", e.getPhone()).isNotEmpty();
        e.setEmail(null);
        String oldPhone = loginPerson.getPhone();
        String newPhone = e.getPhone();
        if (oldPhone.equals(newPhone)) {
            return Rest.createFail(ProjectRestCode.USERNAME_SAME);
        }
        service.checkPersonExist(e);
        SecurityCode securityCode = securityCodeService.saveCodeByPerson(e, SecurityCodeType.RESET_PHONE);
        securityCodeService.sendCode(securityCode);
        //todo shaun 要求， 下个版本更改回来
        String code = securityCode.getCode();
        return Rest.createSuccess(code);
    }

    @PostMapping({"/dr/me/phone"})
    public Rest<Boolean> updatePhoneByCode(@RequestAttribute("loginPerson") Person loginPerson, Person e, String code) throws ParameterException, FailException, ServiceException {
        ParameterExceptionUtil.verify("code", code).isLength(4);
        ParameterExceptionUtil.verify("person.phone ", e.getPhone()).isNotEmpty();
        e.setEmail(null);
        service.checkPersonExist(e);
        e.setId(loginPerson.getId());
        service.updatePhoneByCode(code, e);
        return Rest.createSuccess(true);
    }

    @DeleteMapping({"/dr/me/phone"})
    public Rest<Boolean> deletePhoneAccount(@RequestAttribute("loginPerson") Person loginPerson) throws FailException, ServiceException {
        if (StringUtils.isEmpty(loginPerson.getPhone())) return Rest.createSuccess(true);
        checkAccountDeleteable(loginPerson);
        service.deletePhoneAccount(loginPerson);
        return Rest.createSuccess(true);
    }

    @PostMapping({"/dr/me/email/code"})
    public Rest<String> sendUpdateEmailCode(@RequestAttribute("loginPerson") Person loginPerson, Person e) throws ParameterException, FailException, ServiceException {
        ParameterExceptionUtil.verify("person.email ", e.getEmail()).isNotEmpty();
        e.setPhone(null);
        String oldEmail = loginPerson.getEmail();
        String newEmail = e.getEmail();
        if (oldEmail.equals(newEmail)) {
            return Rest.createFail(ProjectRestCode.USERNAME_SAME);
        }
        service.checkPersonExist(e);
        SecurityCode securityCode = securityCodeService.saveCodeByPerson(e, SecurityCodeType.RESET_EMAIL);
        securityCodeService.sendCode(securityCode);
        //todo shaun 要求， 下个版本更改回来
        String code = securityCode.getCode();
        return Rest.createSuccess(code);
    }

    @PostMapping({"/dr/me/email"})
    public Rest<Boolean> updateEmailByCode(@RequestAttribute("loginPerson") Person loginPerson, Person e, String code) throws ParameterException, FailException, ServiceException {
        ParameterExceptionUtil.verify("code", code).isLength(4);
        ParameterExceptionUtil.verify("person.email ", e.getEmail()).isNotEmpty();
        e.setPhone(null);
        service.checkPersonExist(e);
        e.setId(loginPerson.getId());
        service.updateEmailByCode(code, e);
        return Rest.createSuccess(true);
    }

    @DeleteMapping({"/dr/me/email"})
    public Rest<Boolean> deleteEmailAccount(@RequestAttribute("loginPerson") Person loginPerson) throws FailException, ServiceException {
        String email = loginPerson.getEmail();
        if (StringUtils.isEmpty(email)) return Rest.createSuccess(true);
        checkAccountDeleteable(loginPerson);
        service.deleteEmailAccount(loginPerson);
        return Rest.createSuccess(true);
    }

    private void checkAccountDeleteable(Person person) throws ServiceException {
        String email = person.getEmail();
        String phone = person.getPhone();
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(phone)) {
            throw new ServiceException(ProjectRestCode.ACCOUNT_DEL_FORBIDDEN.getMessage(), ProjectRestCode.ACCOUNT_DEL_FORBIDDEN);
        }
    }
}
