package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Reminder implements Serializable, NotNullObject {
	private Long id;
	private Vehicle vehicle;
	private Inspection inspection;
	private String content;
	private ReminderType type;
	private LocalDate date;
	private LocalDateTime createAt;
	private LocalDateTime updateAt;

	public void fillNotRequire() {
		content = content != null ? content : "";
	}

	public void createAt() {
		createAt = LocalDateTime.now(ZoneId.of("UTC"));
	}

	public void updateAt() {
		updateAt = LocalDateTime.now(ZoneId.of("UTC"));
	}

	public static final Null NULL = new Null();

	private static class Null extends Reminder implements NullObject {
		@Override
		public Vehicle getVehicle() {
			return Vehicle.NULL;
		}
	}

	public static class Builder extends AbstractBuilder {
		@Override
		public Reminder build() {
			try {
				return super.build(Reminder.class);
			} catch (InstantiationException | IllegalAccessException e) {
				return NULL;
			}
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ReminderType getType() {
		return type;
	}

	public void setType(ReminderType type) {
		this.type = type;
	}

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

	public LocalDateTime getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(LocalDateTime updateAt) {
		this.updateAt = updateAt;
	}

	public Inspection getInspection() {
		return inspection;
	}

	public void setInspection(Inspection inspection) {
		this.inspection = inspection;
	}
	
	
	

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Reminder {id=" + id + ", vehicle=" + vehicle + ", inspection=" + inspection + ", content=" + content
				+ ", type=" + type + ", date=" + date + ", createAt=" + createAt + ", updateAt=" + updateAt + "}";
	}

	 

}
