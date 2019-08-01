package com.hiveel.autossav.controller.websocket.model;

public enum ConnectionCode {
    HEARTBEAT(1),
    LOGOUT(4000), MINIMIZE(4001), LOGIN_BY_OTHER_ENDPOINT(4002),
    ;

    private int code;

    ConnectionCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
    public int getCode() {
        return code;
    }
}
