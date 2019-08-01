package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class PersonGroup implements Serializable, NotNullObject {
    private Long id;
    private PersonGroupType type;
    private String name;
    private PersonGroupAuth dashboard;
    private PersonGroupAuth inspection;
    private PersonGroupAuth issues;
    private PersonGroupAuth exam;
    private PersonGroupAuth vehicle;
    private PersonGroupAuth person;
    private PersonGroupAuth invoice;
    private PersonGroupAuth setting;
    private PersonGroupAuth notification;
    private LocalDateTime updateAt;

    public static final Null NULL = new Null();
    private static class Null extends PersonGroup implements NullObject {
    }

    public void fillNotRequire() {
        dashboard = dashboard != null ? dashboard : PersonGroupAuth.NULL;
        inspection = inspection != null ? inspection : PersonGroupAuth.NULL;
        issues = issues != null ? issues : PersonGroupAuth.NULL;
        exam = exam != null ? exam : PersonGroupAuth.NULL;
        vehicle = vehicle != null ? vehicle : PersonGroupAuth.NULL;
        person = person != null ? person : PersonGroupAuth.NULL;
        invoice = invoice != null ? invoice : PersonGroupAuth.NULL;
        setting = setting != null ? setting : PersonGroupAuth.NULL;
        notification = notification != null ? notification : PersonGroupAuth.NULL;
    }
    
	public void updateAt() {
		updateAt =  LocalDateTime.now(ZoneId.of("UTC"));
	}

    public static class Builder extends AbstractBuilder {
        @Override
        public PersonGroup build() {
            try {
                return super.build(PersonGroup.class);
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
    public PersonGroupType getType() {
        return type;
    }

    public void setType(PersonGroupType type) {
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public PersonGroupAuth getDashboard() {
        return dashboard;
    }
    public void setDashboard(PersonGroupAuth dashboard) {
        this.dashboard = dashboard;
    }
    public PersonGroupAuth getInspection() {
        return inspection;
    }
    public void setInspection(PersonGroupAuth inspection) {
        this.inspection = inspection;
    }
    public PersonGroupAuth getIssues() {
        return issues;
    }
    public void setIssues(PersonGroupAuth issues) {
        this.issues = issues;
    }
    public PersonGroupAuth getExam() {
        return exam;
    }
    public void setExam(PersonGroupAuth exam) {
        this.exam = exam;
    }
    public PersonGroupAuth getVehicle() {
        return vehicle;
    }
    public void setVehicle(PersonGroupAuth vehicle) {
        this.vehicle = vehicle;
    }
    public PersonGroupAuth getPerson() {
        return person;
    }
    public void setPerson(PersonGroupAuth person) {
        this.person = person;
    }
    public PersonGroupAuth getInvoice() {
        return invoice;
    }
    public void setInvoice(PersonGroupAuth invoice) {
        this.invoice = invoice;
    }
    public PersonGroupAuth getSetting() {
        return setting;
    }
    public void setSetting(PersonGroupAuth setting) {
        this.setting = setting;
    }
    public PersonGroupAuth getNotification() {
        return notification;
    }
    public void setNotification(PersonGroupAuth notification) {
        this.notification = notification;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Override
	public String toString() {
		return "PersonGroup {id='" + id + "', type='" + type + "', name='" + name + "', dashboard='" + dashboard
				+ "', inspection='" + inspection + "', issues='" + issues + "', exam='" + exam + "', vehicle='"
				+ vehicle + "', person='" + person + "', invoice='" + invoice + "', setting='" + setting
				+ "', notification='" + notification + "', updateAt='" + updateAt + "}";
	}
}
