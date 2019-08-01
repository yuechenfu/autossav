package com.hiveel.autossav.service;

import com.hiveel.auth.sdk.model.Account;
import com.hiveel.autossav.model.ProjectRestCode;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.core.exception.FailException;
import com.hiveel.core.exception.ServiceException;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PersonService {
    int save(Person e);

    void savePersonAndAccount(Person e, Account account) throws FailException;

    boolean delete(Person e) throws FailException;

    void deletePersonAndAccount(Person e) throws FailException;

    void updatePersonAndAccount(Person e, Account account) throws FailException;

    boolean update(Person e);

    Person findById(Person e);

    int count(SearchCondition searchCondition);

    List<Person> find(SearchCondition searchCondition);

    boolean checkEmailExist(String email) throws FailException;

    boolean checkPhoneExist(String phone) throws FailException;

    void checkPersonExist(Person person) throws FailException, ServiceException;

    void updatePhoneByCode(String code, Person person) throws FailException, ServiceException;

    void updateEmailByCode(String code, Person person) throws FailException, ServiceException;

    void deletePhoneAccount(Person person) throws FailException, ServiceException;

    void deleteEmailAccount(Person person) throws FailException, ServiceException;
}
