package com.hiveel.autossav.manager;

import com.hiveel.auth.sdk.model.Account;
import com.hiveel.auth.sdk.service.AccountService;
import com.hiveel.core.exception.FailException;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthManager {
    @Autowired
    private AccountService accountService;

    public Account save(Account e) throws FailException {
        Rest<Account> rest = accountService.save(e);
        if (BasicRestCode.SUCCESS != rest.getCode()) {
            throw new FailException("保存账号异常" );
        }
        return rest.getData();
    }

    public Boolean deleteByPersonId(Account e) throws FailException {
        Rest<Boolean> rest = accountService.deleteByPersonIdAndUsername(e);
        if (BasicRestCode.SUCCESS != rest.getCode()) {
            throw new FailException("删除账号异常");
        }
        return rest.getData();
    }

    public Boolean updateByPersonId(Account e) throws FailException {
        Rest<Boolean> rest = accountService.updateByPersonIdAndUsername(e);
        if (BasicRestCode.SUCCESS != rest.getCode()) {
            throw new FailException("更新账号异常");
        }
        return rest.getData();
    }

    public Boolean checkByUsername(String userName) throws FailException {
        Rest<Boolean> rest = accountService.check(userName);
        if (BasicRestCode.SUCCESS != rest.getCode()) {
            throw new FailException("查询异常");
        }
        return rest.getData();
    }

    public Account findAccountByToken(String token) {
        Rest<Account> rest = accountService.me(token);
        if (BasicRestCode.SUCCESS != rest.getCode()) {
            return Account.NULL;
        }
        return rest.getData();
    }

    public Account findAccountByPersonId(Account e) throws FailException {
        Rest<Account> rest = accountService.findByPersonId(e);
        if (BasicRestCode.SUCCESS != rest.getCode()) {
            throw new FailException("查询异常");
        }
        return rest.getData();
    }
}
