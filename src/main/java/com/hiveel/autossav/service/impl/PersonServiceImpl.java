package com.hiveel.autossav.service.impl;

import java.util.List;

import com.hiveel.auth.sdk.model.Account;
import com.hiveel.autossav.manager.AuthManager;
import com.hiveel.autossav.model.ProjectRestCode;
import com.hiveel.autossav.model.entity.SecurityCodeType;
import com.hiveel.autossav.service.SecurityCodeService;
import com.hiveel.core.exception.FailException;
import com.hiveel.core.exception.ServiceException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hiveel.autossav.dao.PersonDao;
import com.hiveel.autossav.dao.PersonGroupDao;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.service.PersonService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    private PersonDao dao;
    @Autowired
    private PersonGroupDao groupDao;
    @Autowired
    private AuthManager authManager;
    @Autowired
    private SecurityCodeService securityCodeService;

    @Override
    public int save(Person e) {
        e.fillNotRequire();
        e.createAt();
        e.updateAt();
        return dao.save(e);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void savePersonAndAccount(Person e, Account account) throws FailException {
        save(e);
        String name  = securityCodeService.getName(e);
        Account saveTo = new Account.Builder()
                .set("username", name)
                .set("personId", String.valueOf(e.getId()))
                .set("password", account.getPassword())
                .set("extra", String.valueOf(e.getType())).build();
        authManager.save(saveTo);
    }

    @Override
    public boolean delete(Person e) {
        return dao.delete(e) == 1;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deletePersonAndAccount(Person e) throws FailException {
        e = findById(e);
        if (!delete(e)) {
            throw new FailException("delete fail");
        }
        if(!StringUtils.isEmpty(e.getPhone())){
            authManager.deleteByPersonId(new Account.Builder().set("personId", e.getId().toString()).set("username",e.getPhone()).build());
        }
        if(!StringUtils.isEmpty(e.getEmail())){
            authManager.deleteByPersonId(new Account.Builder().set("personId", e.getId().toString()).set("username",e.getEmail()).build());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePersonAndAccount(Person e, Account account) throws FailException {
        e.updateAt();
        dao.update(e);
        if (!StringUtils.isEmpty(e.getEmail()) || !StringUtils.isEmpty(e.getPhone())
                || account.getPassword() != null || e.getType() != null) {
            Account updateAccount = new Account.Builder().set("personId", String.valueOf(e.getId())).build();
            String username = securityCodeService.getName(e);
            updateAccount.setUsername(username);
            updateAccount.setPassword(account.getPassword());
            if (e.getType() != null) updateAccount.setExtra(e.getType().toString());
            authManager.updateByPersonId(updateAccount);
        }
    }

    @Override
    public boolean update(Person e) {
        e.updateAt();
        return dao.update(e) == 1;
    }

    @Override
    public Person findById(Person e) {
        Person result = dao.findById(e);
        return result != null ? result : Person.NULL;
    }

    @Override
    public int count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<Person> find(SearchCondition searchCondition) {
        searchCondition.setDefaultSortBy("updateAt");
        return dao.find(searchCondition);
    }

    @Override
    public boolean checkEmailExist(String email) throws FailException {
        int personCount = dao.countByEmail(email);
        boolean accountExist = authManager.checkByUsername(email);
        return (personCount > 0 || accountExist);
    }

    @Override
    public boolean checkPhoneExist(String phone) throws FailException {
        int personCount = dao.countByPhone(phone);
        boolean accountExist = authManager.checkByUsername(phone);
        return (personCount > 0 || accountExist);
    }

    @Override
    public void checkPersonExist(Person person) throws FailException, ServiceException {
        String email = person.getEmail();
        String phone = person.getPhone();
        if (!StringUtils.isEmpty(email) && checkEmailExist(email)) {
            throw new ServiceException(ProjectRestCode.USERNAME_REGISTERED.getMessage(), ProjectRestCode.USERNAME_REGISTERED);
        }
        if (!StringUtils.isEmpty(phone) && checkPhoneExist(phone)) {
            throw new ServiceException(ProjectRestCode.USERNAME_REGISTERED.getMessage(), ProjectRestCode.USERNAME_REGISTERED);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePhoneByCode(String code, Person person) throws FailException, ServiceException {
        String name = securityCodeService.getName(person);
        securityCodeService.verifyCodeAndUsed(code, name, SecurityCodeType.RESET_PHONE);
        updatePersonAndAccount(person, new Account());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateEmailByCode(String code, Person person) throws FailException, ServiceException {
        String name = securityCodeService.getName(person);
        securityCodeService.verifyCodeAndUsed(code, name, SecurityCodeType.RESET_EMAIL);
        updatePersonAndAccount(person, new Account());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deletePhoneAccount(Person person) throws FailException, ServiceException {
        String oldPhone = person.getPhone();
        person.setPhone("");
        dao.update(person);
        Account deleteAccount = new Account.Builder().set("personId", person.getId().toString()).set("username",oldPhone).build();
        authManager.deleteByPersonId(deleteAccount);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteEmailAccount(Person person) throws FailException, ServiceException {
        String oldEmail = person.getEmail();
        person.setEmail("");
        dao.update(person);
        Account deleteAccount = new Account.Builder().set("personId", person.getId().toString()).set("username",oldEmail).build();
        authManager.deleteByPersonId(deleteAccount);
    }



    public void setAuthManager(AuthManager authManager) {
        this.authManager = authManager;
    }
}
