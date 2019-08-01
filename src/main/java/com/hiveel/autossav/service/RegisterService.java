package com.hiveel.autossav.service;

import com.hiveel.auth.sdk.model.Account;
import com.hiveel.autossav.model.ProjectRestCode;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.SecurityCode;
import com.hiveel.core.exception.FailException;
import com.hiveel.core.exception.ServiceException;

public interface RegisterService {
    void registerByCode(String code, Person person, Account account) throws FailException, ServiceException;
}
