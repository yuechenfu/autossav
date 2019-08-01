package com.hiveel.autossav.model.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SendGridEmailTemplate {

    private static String REGISTER_NOTIFY;
    private static String REGISTER_EMAIL;
    private static String INSPECTION_THREE_DAYS;
    private static String INSPECTION_TODAY;
    private static String INSPECTION_COMPLETE;
    private static String ISSUE_CONFIRM;
    private static String VERIFY_EMAIL;

    public static String REGISTER_NOTIFY(){
        return REGISTER_NOTIFY;
    }
    public static String REGISTER_EMAIL(){
        return REGISTER_EMAIL;
    }
    public static String INSPECTION_THREE_DAYS(){
        return INSPECTION_THREE_DAYS;
    }
    public static String INSPECTION_TODAY(){
        return INSPECTION_TODAY;
    }
    public static String INSPECTION_COMPLETE(){
        return INSPECTION_COMPLETE;
    }
    public static String ISSUE_CONFIRM(){
        return ISSUE_CONFIRM;
    }
    public static String VERIFY_EMAIL(){
        return VERIFY_EMAIL;
    }
    @Value("${core.sendgrid.templates.REGISTER_NOTIFY}")
    public void setRegisterNotify(String registerNotify) {
        REGISTER_NOTIFY = registerNotify;
    }
    @Value("${core.sendgrid.templates.REGISTER_EMAIL}")
    public void setResetPassword(String registerEmail) {
        REGISTER_EMAIL = registerEmail;
    }
    @Value("${core.sendgrid.templates.INSPECTION_THREE_DAYS}")
    public void setInspectionThreeDays(String inspectionThreeDays) {
        INSPECTION_THREE_DAYS = inspectionThreeDays;
    }
    @Value("${core.sendgrid.templates.INSPECTION_TODAY}")
    public void setInspectionToday(String inspectionToday) {
        INSPECTION_TODAY = inspectionToday;
    }
    @Value("${core.sendgrid.templates.INSPECTION_COMPLETE}")
    public void setInspectionComplete(String inspectionComplete) {
        INSPECTION_COMPLETE = inspectionComplete;
    }
    @Value("${core.sendgrid.templates.ISSUE_CONFIRM}")
    public void setIssueConfirm(String issueConfirm) {
        ISSUE_CONFIRM = issueConfirm;
    }
    @Value("${core.sendgrid.templates.VERIFY_EMAIL}")
    public void setVerifyEmail(String verifyEmail) {
        VERIFY_EMAIL = verifyEmail;
    }
}
