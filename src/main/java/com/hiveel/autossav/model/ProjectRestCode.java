package com.hiveel.autossav.model;

import com.hiveel.core.model.rest.RestCode;

public enum ProjectRestCode implements RestCode {
	HAS_BEEN_REGISTERED(20,"This car has been registered"),
    USERNAME_SAME(19,"username not changed"),
    USERNAME_REGISTERED(21,"This email has been registered to another user"),
    SECURITY_CODE_EXPIRED(22,"This code already expired"),
    SECURITY_CODE_WRONG(22,"invalid code entered"),
    ACCOUNT_DEL_FORBIDDEN(23,"you can not delete account's information now"),
    ;
    private int code;
    private String message;

    ProjectRestCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
    @Override
    public int getCode() {
        return code;
    }
    @Override
    public String getMessage() {
        return message;
    }

}
