package com.hiveel.autossav.job;

import com.hiveel.autossav.dao.*;
import com.hiveel.autossav.manager.PushManager;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.conf.SendGridEmailTemplate;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.core.log.util.LogUtil;
import com.hiveel.core.util.SendGridEmailUtil;
import com.hiveel.core.util.ThreadUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class PushJob {
    @Autowired
    private PlanDao planDao;
    @Autowired
    private VehicleDriverRelateDao vehicleDriverRelateDao;
    @Autowired
    private VehicleDao vehicleDao;
    @Autowired
    private PersonDao personDao;
    @Autowired
    private PushManager pushManager;
    @Autowired
    private AddressDao addressDao;
    @Autowired
    private SendGridEmailUtil emailUtil;


    private final static int MAX_TASK = 3;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(MAX_TASK);

    //每天早上9点提醒3天后要送检的车
    @Scheduled(cron = "0 0 9 * * ?")
    public void pushBeforeInspectionTimer() {
        List<Plan> planList = findPlanForInspection(LocalDateTime.now(ZoneId.of("UTC")).plusDays(3));
        LogUtil.debug("准备推送 3天后要送检的车辆提醒，记录数据：" + planList.size());
        planList.stream().forEach(plan -> {
            VehicleDriverRelate vehicleDriverRelate = findVehicleDriverRelate(plan);
            if (!vehicleDriverRelate.isNull()) {
                Person person = vehicleDriverRelate.getDriver();
                Vehicle vehicle = vehicleDriverRelate.getVehicle();
//                threadPool.submit(() -> {
//
//                });
                PushRecord pushRecord = new PushRecord.Builder().set("person", person).set("type", PushRecordType.PLAN_THREE_DAYS).build();
                pushRecord.setRelateId(plan.getId());
                pushRecord.addPayLoad("plan", plan).addPayLoad("vehicleDriverRelate", vehicleDriverRelate).addPayLoad("vehicle",vehicle);
                pushManager.pushAndSaveRecord(pushRecord);
                sendMail(person, plan, PushRecordType.PLAN_THREE_DAYS);
            }
        });
    }

    //每天早上8点 提醒今天要送检的车
    @Scheduled(cron = "0 0 8 * * ?")
    public void pushTodayInspectionTimer() {
        List<Plan> planList = findPlanForInspection(LocalDateTime.now(ZoneId.of("UTC")));
        LogUtil.debug("准备推送 今天要送检的车辆提醒，记录数据：" + planList.size());
        planList.stream().forEach(plan -> {
            VehicleDriverRelate vehicleDriverRelate = findVehicleDriverRelate(plan);
            if (!vehicleDriverRelate.isNull()) {
                Vehicle vehicle = vehicleDriverRelate.getVehicle();
                Person person = vehicleDriverRelate.getDriver();
                threadPool.submit(() -> {
                    PushRecord pushRecord = new PushRecord.Builder().set("person", person).set("type", PushRecordType.PLAN_TODAY).build();
                    pushRecord.setRelateId(plan.getId());
                    pushRecord.addPayLoad("plan", plan).addPayLoad("vehicleDriverRelate", vehicleDriverRelate).addPayLoad("vehicle",vehicle);
                    pushManager.pushAndSaveRecord(pushRecord);
                    sendMail(person, plan, PushRecordType.PLAN_TODAY);
                });
            }
        });
    }

    private void sendMail(Person person, Plan plan, PushRecordType pushType) {
        String email = person.getEmail();
        if (StringUtils.isEmpty(email)) {
            return;
        }
        try {
            String templateId = null;
            if (pushType == PushRecordType.PLAN_THREE_DAYS) {
                templateId = SendGridEmailTemplate.INSPECTION_THREE_DAYS();
            } else {
                templateId = SendGridEmailTemplate.INSPECTION_TODAY();
            }
            Address address = addressDao.findById(plan.getAddress());
            Map<String, Object> data = new HashMap<>();
            data.put("inspectionLocation", address.getContent());
            int day = plan.getDay();
            LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
            int nowDay = now.getDayOfMonth();
            Month month = now.getMonth();
            int year = now.getYear();
            if (nowDay > day) {
                LocalDateTime nextMonth = now.plusMonths(1);
                month = nextMonth.getMonth();
                year = nextMonth.getYear();
            }
            String dateInfo = month.toString() + " " + day + "st," + year;
            data.put("inspectionDate", dateInfo);
            String subject = "Scheduled inspection reminder";
            emailUtil.sendByTemplate(email, subject, templateId, data);
        } catch (Exception e) {
            LogUtil.error("send email fail" + email, e);
        }
    }

    private List<Plan> findPlanForInspection(LocalDateTime dateTime) {
        int day = dateTime.getDayOfMonth();
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setLimit(0);
        searchCondition.setMinDate(String.valueOf(day));
        searchCondition.setMaxDate(String.valueOf(day));
        return planDao.find(searchCondition);
    }

    private VehicleDriverRelate findVehicleDriverRelate(Plan plan) {
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setLimit(0);
        searchCondition.setMinDate("");
        searchCondition.setMaxDate("");
        searchCondition.setDefaultSortBy("id");
        List<VehicleDriverRelate> list = vehicleDriverRelateDao.findByVehicle(searchCondition, plan.getVehicle());
        if (list.isEmpty()) {
            return VehicleDriverRelate.NULL;
        }
        VehicleDriverRelate inDb = list.get(0);
        Vehicle vehicle = vehicleDao.findById(inDb.getVehicle());
        Person driver = personDao.findById(inDb.getDriver());
        inDb.setVehicle(vehicle);
        inDb.setDriver(driver);
        return inDb;
    }

    //for testcase usage
    public void setEmailUtil(SendGridEmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }
}
