package com.hiveel.autossav.controller.websocket.util;

import com.hiveel.autossav.controller.websocket.model.ConnectionCode;
import org.springframework.web.socket.CloseStatus;

public class CloseStatusUtil {
    public static CloseStatus logout() {
        return new CloseStatus(Integer.parseInt(ConnectionCode.LOGOUT.toString()));
    }

    public static CloseStatus minimize() {
        return new CloseStatus(Integer.parseInt(ConnectionCode.MINIMIZE.toString()));
    }

    public static CloseStatus loginByOtherEndpoint() {
        return new CloseStatus(Integer.parseInt(ConnectionCode.LOGIN_BY_OTHER_ENDPOINT.toString()));
    }
}