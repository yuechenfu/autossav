package com.hiveel.autossav.model.entity;

public enum PushRecordType {

    PLAN_THREE_DAYS, PLAN_TODAY, INSPECTION_COMPLETE, ISSUE_UPDATE_APPT,
    WS_ISSUE_CREATE, WS_ISSUE_QUOTE, WS_ISSUE_SCHEDULED, WS_ISSUE_CONFIRM, WS_ISSUE_COMPLETE,
    WS_INSPECTION_CONFIRM, WS_INSPCTION_QUOTE, WS_INSPECTION_COMPLETE,
    WS_VehicleDriverRelate_SIGNIN, WS_VehicleDriverRelate_SIGNOUT,
    ;

    public static String getContent(PushRecord pushRecord){
        PushRecordType type = pushRecord.getType();
        String content = "how are you today";
        String[] args = new String[]{};
        Vehicle vehicle = (Vehicle)pushRecord.getPayLoad().get("vehicle");
        switch (type){
            case PLAN_TODAY:
                content = "Your scheduled monthly vehicle inspection is today. We look forward to serving you soon!";
                break;
            case PLAN_THREE_DAYS:
                content = "Your scheduled monthly vehicle inspection is due in 3 days. We look forward to serving you soon!";
                break;
            case INSPECTION_COMPLETE:
                args = new String[]{vehicle.getName() };
                content = "The inspection of your vehicle \"%s\" has been completed recently with us.";
                break;
            case ISSUE_UPDATE_APPT:
                content = "We have received your issue report recently, the following is the service detail based on your issue.";
                break;
            case WS_ISSUE_CREATE:
                content = "You have received a new issue report.";
                break;
            case WS_ISSUE_CONFIRM:
                content = "Fleet manager has confirmed the issue quote.";
                break;
            case WS_ISSUE_QUOTE:
                content = "You have received a updated issue quote.";
                break;
            case WS_ISSUE_SCHEDULED:
                content = "Driver has confirmed the service details.";
                break;
            case WS_INSPECTION_CONFIRM:
                content = "Fleet manager has confirmed to inspection quote.";
                break;
            case WS_ISSUE_COMPLETE:
                content = "A service has been completed.";
                break;
            case WS_INSPCTION_QUOTE:
                content = "You have received a new inspection quote.";
                break;
            case WS_INSPECTION_COMPLETE:
                content = "A service has been completed.";
                break;
            case WS_VehicleDriverRelate_SIGNIN:
                content = "A driver have active a vehicle.";
                break;
            case WS_VehicleDriverRelate_SIGNOUT:
                content = "A driver have inactive a vehicle.";
                break;
        }
        return String.format(content,args);
    }

    public static String getSubject(PushRecord pushRecord){
        PushRecordType type = pushRecord.getType();
        String content = "how are you today";
        switch (type){
            case PLAN_TODAY:
                content = "Scheduled today";
                break;
            case PLAN_THREE_DAYS:
                content = "Scheduled in three days";
                break;
            case INSPECTION_COMPLETE:
                content = "Inspection Complete";
                break;
            case ISSUE_UPDATE_APPT:
                content = "Issue report received";
                break;
            case WS_ISSUE_CREATE:
                content = "You have received a new issue report.";
                break;
            case WS_ISSUE_CONFIRM:
                content = "Issue Confirmed";
                break;
            case WS_ISSUE_QUOTE:
                content = "Issue Quoted";
                break;
            case WS_ISSUE_SCHEDULED:
                content = "Issue Scheduled";
                break;
            case WS_INSPECTION_CONFIRM:
                content = "Inspection Confirmed";
                break;
            case WS_ISSUE_COMPLETE:
                content = "A service has been completed.";
                break;
            case WS_INSPCTION_QUOTE:
                content = "Inspection Quoted";
                break;
            case WS_INSPECTION_COMPLETE:
                content = "Inspection Complete";
                break;
            case WS_VehicleDriverRelate_SIGNIN:
                content = "Sign in";
                break;
            case WS_VehicleDriverRelate_SIGNOUT:
                content = "Sign out";
                break;
        }
        return content;
    }

}
