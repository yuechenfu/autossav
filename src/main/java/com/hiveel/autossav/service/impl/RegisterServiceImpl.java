package com.hiveel.autossav.service.impl;

import com.hiveel.auth.sdk.model.Account;
import com.hiveel.autossav.model.ProjectRestCode;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.autossav.service.SecurityCodeService;
import com.hiveel.autossav.service.RegisterService;
import com.hiveel.core.exception.FailException;
import com.hiveel.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private PersonService personService;
    @Autowired
    private SecurityCodeService securityCodeService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void registerByCode(String code, Person person, Account account) throws FailException, ServiceException {
        String name = securityCodeService.getName(person);
        securityCodeService.verifyCodeAndUsed(code, name, SecurityCodeType.REGISTER);
        setPersonDefaultProp(person);
        personService.savePersonAndAccount(person, account);
    }

    private void setPersonDefaultProp(Person person) {
        person.setType(PersonType.DR_PRIVATE);
        person.setGroup(new PersonGroup.Builder().set("id", 0L).build());
    }
}
