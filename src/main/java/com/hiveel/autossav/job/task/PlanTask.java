package com.hiveel.autossav.job.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hiveel.autossav.service.InspectionService;
import com.hiveel.autossav.service.PlanService;
import com.hiveel.autossav.service.ReminderService;
import com.hiveel.autossav.service.VehicleDriverRelateService;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Inspection;
import com.hiveel.autossav.model.entity.InspectionStatus;
import com.hiveel.autossav.model.entity.Person;
import com.hiveel.autossav.model.entity.Plan;
import com.hiveel.autossav.model.entity.Reminder;
import com.hiveel.autossav.model.entity.ReminderType;
import com.hiveel.autossav.model.entity.VehicleDriverRelate;

@Component
public class PlanTask implements Runnable {

	@Autowired
	private PlanService service;

	@Autowired
	private ReminderService reminderService;

	@Autowired
	private VehicleDriverRelateService vehicleDriverRelateService;

	@Autowired
	private InspectionService inspectionService;

	public PlanTask() {
	}

	@Override
	public void run() {
		DayReminderForInspection(ReminderType.INSPECTION_3DAY);
		DayReminderForInspection(ReminderType.INSPECTION_1DAY);
		updateStatusFromPendingToOverdue(InspectionStatus.OVERDUE);
	}
	private void DayReminderForInspection(ReminderType type) {
		int day = type == ReminderType.INSPECTION_1DAY ? 1 : 3 ;
		LocalDate inspectionDate = LocalDate.now().plusDays(day);
		Integer lenDate = LocalDate.now().lengthOfMonth();
		Integer minDate = inspectionDate.getDayOfMonth();
		Integer maxDate = (minDate == lenDate) ? 31 : minDate ;
		SearchCondition searchCondition = new SearchCondition();
		searchCondition.setMinDate(minDate.toString());
		searchCondition.setMaxDate(maxDate.toString());
		List<Plan> list = service.find(searchCondition);
		list.stream().parallel().forEach(e -> {
			Inspection inspection = Inspection.NULL;
			if (type == ReminderType.INSPECTION_1DAY) inspection = findByVehicle(e, inspectionDate.toString());
			if (type == ReminderType.INSPECTION_3DAY) inspection = saveInspection(e, inspectionDate.toString());
			if(inspection.getId() == null) return ;
			Reminder reminder = new Reminder.Builder().set("vehicle", e.getVehicle()).set("content", day + "-day reminder for the monthly inspection").set("type", type).set("date", inspectionDate).set("inspection", inspection).build();
			reminderService.save(reminder);
		});
	}
	private Inspection findByVehicle(Plan plan, String inspectionDate) {
		SearchCondition searchCondition = new SearchCondition();
		searchCondition.setMinDate(inspectionDate);
		searchCondition.setMaxDate(inspectionDate);
		List<Inspection> list = inspectionService.findByVehicle(searchCondition, plan.getVehicle());
		return list.size() >0 ? list.get(0) : Inspection.NULL;
	}
	private Inspection saveInspection(Plan plan, String inspectionDate) {
		SearchCondition searchCondition = new SearchCondition();
		searchCondition.setOffDate("");
		List<VehicleDriverRelate> list = vehicleDriverRelateService.findByVehicle(searchCondition, plan.getVehicle());
		if(list.size() == 0) return Inspection.NULL;
		VehicleDriverRelate vehicleDriverRelate =  list.get(0) ;
		Person autosave = new Person.Builder().set("id", 0L).build();
		Inspection result = new Inspection.Builder().set("vehicle", plan.getVehicle()).set("driver", vehicleDriverRelate.getDriver()).set("autosave", autosave)
				.set("address", plan.getAddress()).set("date", inspectionDate).set("name", "inspection 3-day "+inspectionDate).set("odometer", 0).set("status", InspectionStatus.PENDING).build();
		int count = inspectionService.saveByPlanJob(result);
		return count >0 ? result : Inspection.NULL;
	}
	private void updateStatusFromPendingToOverdue(InspectionStatus status) {
		LocalDate localLastDay = LocalDate.now(ZoneId.of("UTC"));
		Inspection e = new Inspection.Builder().set("status", status).set("date", localLastDay.toString()).build();
		inspectionService.updateStatusByPendingAndDate(e);
	}
}
