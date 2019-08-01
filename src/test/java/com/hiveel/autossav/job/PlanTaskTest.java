package com.hiveel.autossav.job;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.hiveel.autossav.job.task.PlanTask;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Address;
import com.hiveel.autossav.model.entity.Inspection;
import com.hiveel.autossav.model.entity.InspectionStatus;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.Plan;
import com.hiveel.autossav.model.entity.ReminderType;
import com.hiveel.autossav.model.entity.Vehicle;
import com.hiveel.autossav.service.InspectionService;
import com.hiveel.autossav.service.PlanService;
import com.hiveel.autossav.service.ReminderService;
import com.hiveel.core.util.DateUtil;
import com.hiveel.core.util.SendGridEmailUtil;
import com.hiveel.core.util.ThreadUtil;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PlanTaskTest {
	
	@Autowired
	private PlanTask planTask;
	@Autowired
    private PlanService service;
	@Autowired
    private InspectionService inspectionService;
	@Autowired
    private ReminderService reminderService;
	@Autowired
    private SendGridEmailUtil sendGridEmailUtil;
	    
	@Test
    public void planTaskTest() {
		// 模拟2条plan， 分别为当前日1 、 3 天后的plan 记录
		LocalDate localToday = LocalDate.now();
		LocalDate inspectionDate1 = localToday.plusDays(1);
		
		Integer minDate = inspectionDate1.getDayOfMonth();
		Vehicle vehicle1 = new Vehicle.Builder().set("id", 1L).build();
        Address address1 = new Address.Builder().set("id", 1L).build();
        Plan e1 = new Plan.Builder().set("vehicle", vehicle1).set("address", address1).set("day", minDate).build();
        service.save(e1);
        LocalDate inspectionDate3 = localToday.plusDays(3);
		minDate = inspectionDate3.getDayOfMonth();
        Vehicle vehicle3 = new Vehicle.Builder().set("id", 1L).build();
        Address address3 = new Address.Builder().set("id", 1L).build();
        Plan e3 = new Plan.Builder().set("vehicle", vehicle3).set("address", address3).set("day", minDate).build();
        service.save(e3);
        
       
        //模拟1天后的inspection记录
        Vehicle vehicle = new Vehicle.Builder().set("id", 1L).set("name", "CAL-2421").build();
        Person driver = new Person.Builder().set("id", 1L).set("email", "ryan@hiveel.com").build(); 	
        Person autosave = new Person.Builder().set("id", 3L).build(); 
        Address address = new Address.Builder().set("id", 1L).build();
        Inspection e = new Inspection.Builder().set("vehicle", vehicle).set("driver", driver).set("autosave", autosave).set("address", address).set("date", inspectionDate1.toString() ).set("odometer", 0)
        		.set("name", "inspection 1-day").set("status", InspectionStatus.PENDING).build();
        inspectionService.saveByPlanJob(e);
        //Assertions.assertDoesNotThrow(()->{sendGridEmailUtil.send(driver.getEmail(), "inspection 1-day", "inspection 1-day for "+ vehicle.getName());});	
                
        
    	planTask.run();    	
    	SearchCondition searchCondition = new SearchCondition();
		searchCondition.setMinDate(inspectionDate1.toString());
		searchCondition.setMaxDate(inspectionDate1.toString());
    	List<Inspection> list = inspectionService.findByVehicle(searchCondition, vehicle);
    	Assert.assertEquals(1, list.size());
        service.delete(e1);
        service.delete(e3);
        inspectionService.delete(e);   	
    }
	
}
