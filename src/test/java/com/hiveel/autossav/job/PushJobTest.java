package com.hiveel.autossav.job;

import com.hiveel.autossav.dao.AddressDao;
import com.hiveel.autossav.manager.PushManager;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.conf.SendGridEmailTemplate;
import com.hiveel.autossav.model.entity.*;
import com.hiveel.autossav.service.*;
import com.hiveel.core.log.util.LogUtil;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.util.DateUtil;
import com.hiveel.core.util.SendGridEmailUtil;
import com.hiveel.core.util.ThreadUtil;
import com.hiveel.push.sdk.service.PushService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PushJobTest {
    @Autowired
    private PushJob pushJob;

    @Autowired
    private PlanService planService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private PersonService personService;
    @Autowired
    private VehicleDriverRelateService vehicleDriverRelateService;
    @Autowired
    private PushRecordService pushRecordService;


    private Person personForTest;
    private Vehicle vehicleForTest;
    private Person personForTest2;
    private Vehicle vehicleForTest2;
    private Plan planForTestThreeDaysLater;
    private Plan planForTestToday;
    private VehicleDriverRelate vehicleDriverRelateForTest;
    private VehicleDriverRelate vehicleDriverRelateForTest2;

    @Test
    public void pushBeforeInspectionTimer() throws Exception {
        pushJob.pushBeforeInspectionTimer();
        Thread.sleep(100l);
        //验证已经发送的推送
        List<PushRecord> list = pushRecordService.find(new SearchCondition());
        PushRecord pushRecord = list.get(list.size()-1);
        Assert.assertEquals(personForTest.getId(), pushRecord.getPerson().getId());
        Vehicle vehicle = (Vehicle)pushRecord.getPayLoad().get("vehicle");
        Assert.assertEquals(vehicleForTest.getId(),vehicle.getId());

        verifyEmailSended(pushRecord);
        //清理产生的数据
        pushRecordService.delete(pushRecord);
    }

    @Autowired
    private AddressDao addressDao;

    //verify the send email method has been called
    public void verifyEmailSended(PushRecord pushRecord) throws Exception {
        Plan plan = (Plan) pushRecord.getPayLoad().get("plan");
        Person person = pushRecord.getPerson();
        person = personService.findById(person);
        String email = person.getEmail();
        PushRecordType pushType = pushRecord.getType();
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
        Mockito.verify(mockSendEmailUitl).sendByTemplate(email, subject, templateId, data);
    }

    @Test
    public void pushTodayInspectionTimer() throws Exception{
        pushJob.pushTodayInspectionTimer();
        Thread.sleep(100l);
        //验证已经发送的推送
        List<PushRecord> list = pushRecordService.find(new SearchCondition());
        PushRecord pushRecord = list.get(list.size()-1);
        Assert.assertEquals(personForTest2.getId(), pushRecord.getPerson().getId());
        Vehicle vehicle = (Vehicle)pushRecord.getPayLoad().get("vehicle");
        Assert.assertEquals(vehicleForTest2.getId(),vehicle.getId());
        verifyEmailSended(pushRecord);
        //清理产生的数据
        pushRecordService.delete(pushRecord);
    }

    @Autowired
    private PushManager pushManager;


    private PushService mockPushService = Mockito.mock(PushService.class);
    private SendGridEmailUtil mockSendEmailUitl = Mockito.mock(SendGridEmailUtil.class);


    @Before
    public void prepareTestData() {
        PersonGroup personGroup = new PersonGroup.Builder().set("id", 3L).set("name", "CarManager").build();
        personForTest = new Person.Builder().set("group", personGroup).set("firstName", "User").set("lastName", "D").set("phone", "1233232342")
                .set("email", "user_x@hiveel.com").set("driverLicense", "13dsfr422e").set("type", PersonType.DR).set("imgsrc", "").build();
        personService.save(personForTest);

        personForTest2 = new Person.Builder().set("group", personGroup).set("firstName", "User").set("lastName", "D").set("phone", "1233232342")
                .set("email", "user_x@hiveel.com").set("driverLicense", "13dsfr422e").set("type", PersonType.DR).set("imgsrc", "").build();
        personService.save(personForTest2);

        VehicleGroup group = new VehicleGroup.Builder().set("id", 1L).set("name", "Group 1").set("content", " ").build();
        vehicleForTest = new Vehicle.Builder().set("name", "carx").set("group", group)
                .set("status", VehicleStatus.ACTIVE).set("vin", "12345678901234567").set("plate", "few-243").set("rental", false).build();
        vehicleService.save(vehicleForTest);

        vehicleForTest2 = new Vehicle.Builder().set("name", "carx").set("group", group)
                .set("status", VehicleStatus.ACTIVE).set("vin", "12345678901234567").set("plate", "few-243").set("rental", false).build();
        vehicleService.save(vehicleForTest2);

        int day = LocalDateTime.now(ZoneId.of("UTC")).plusDays(3).getDayOfMonth();
        Address address = new Address.Builder().set("id", 1L).build();
        planForTestThreeDaysLater = new Plan.Builder().set("vehicle", vehicleForTest).set("address", address).set("day", day).build();
        planService.save(planForTestThreeDaysLater);

        day = LocalDateTime.now(ZoneId.of("UTC")).getDayOfMonth();
        planForTestToday = new Plan.Builder().set("vehicle", vehicleForTest2).set("address", address).set("day", day).build();
        planService.save(planForTestToday);

        vehicleDriverRelateForTest = new VehicleDriverRelate.Builder().set("vehicle", vehicleForTest).set("driver", personForTest).set("onDate", DateUtil.newUtcTimeInstance()).set("onOdometer", "14215").build();
        vehicleDriverRelateService.save(vehicleDriverRelateForTest);

        vehicleDriverRelateForTest2 = new VehicleDriverRelate.Builder().set("vehicle", vehicleForTest2).set("driver", personForTest2).set("onDate", DateUtil.newUtcTimeInstance()).set("onOdometer", "14215").build();
        vehicleDriverRelateService.save(vehicleDriverRelateForTest2);

        Mockito.when(mockPushService.sendSingle(Mockito.any(), Mockito.any())).thenReturn(Rest.createSuccess(true));
        mockPushService = Mockito.mock(PushService.class);
        mockSendEmailUitl = Mockito.mock(SendGridEmailUtil.class);
        Mockito.when(mockPushService.sendSingle(Mockito.any(),Mockito.any())).thenReturn(Rest.createSuccess(true));
        pushManager.setPushService(mockPushService);
        pushJob.setEmailUtil(mockSendEmailUitl);
    }

    @After
    public void clearTestData() throws Exception {
        personService.delete(personForTest);
        vehicleService.delete(vehicleForTest);
        planService.delete(planForTestThreeDaysLater);
        vehicleDriverRelateService.delete(vehicleDriverRelateForTest);

        personService.delete(personForTest2);
        vehicleService.delete(vehicleForTest2);
        planService.delete(planForTestToday);
        vehicleDriverRelateService.delete(vehicleDriverRelateForTest2);
    }

}
