package com.hiveel.autossav.model.entity;

public enum SecurityCodeType {
    RESET_EMAIL, RESET_PHONE, REGISTER,
    ;

    public static String getContent(SecurityCode securityCode){
        SecurityCodeType type = securityCode.getType();
        String content = "how are you today";
        String[] args = new String[]{securityCode.getCode()};
        switch (type){
            case RESET_EMAIL:
                content = "You can change your email by enter this 4-digit code in your app. The code is: %s  ";
                break;
            case RESET_PHONE:
                content = "[autossav] You can update your phone by enter this 4-digit code in your app. The code is: %s  ";
                break;
            case REGISTER:
                content = "[autossav] You can complete sign up by enter this 4-digit code in your app. The code is: %s ";
                break;
        }
        return String.format(content,args);
    }

    public static String getTitle(SecurityCode securityCode){
        SecurityCodeType type = securityCode.getType();
        String content = " ";
        switch (type){
            case RESET_EMAIL:
                content = "ready to update email address";
                break;
            case RESET_PHONE:
                content = "ready to update phone number";
                break;
            case REGISTER:
                content = "ready to register";
                break;
        }
        return content;
    }
}
