package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Odometer implements Serializable, NotNullObject {
    private Long id;
    private Long relateId;
    private OdometerType type;
    private Integer mi;
    private String date;
    private Vehicle vehicle;
    private LocalDateTime updateAt;

    private Inspection inspection;
    private Issue issue;

    public static final Null NULL = new Null();
    private static class Null extends Odometer implements NullObject {
        @Override
        public Issue getIssue() {return Issue.NULL;}
        @Override
        public Inspection getInspection() {return Inspection.NULL;}
        @Override
        public Vehicle getVehicle() {return Vehicle.NULL;}
    }

    public static class Builder extends AbstractBuilder {
        @Override
        public Odometer build() {
            try {
                return super.build(Odometer.class);
            } catch (InstantiationException | IllegalAccessException e) {
                return NULL;
            }
        }
    }

    public void updateAt() {
        updateAt =  LocalDateTime.now(ZoneId.of("UTC"));
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRelateId() {
		return relateId;
	}

	public void setRelateId(Long relateId) {
		this.relateId = relateId;
	}

	public OdometerType getType() {
		return type;
	}

	public void setType(OdometerType type) {
		this.type = type;
	}

	public Integer getMi() {
		return mi;
	}

	public void setMi(Integer mi) {
		this.mi = mi;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
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

	public Issue getIssue() {
		return issue;
	}

	public void setIssue(Issue issue) {
		this.issue = issue;
	}

	@Override
	public String toString() {
		return "Odometer {id='" + id + "', relateId='" + relateId + "', type='" + type + "', mi='" + mi + "', date='"
				+ date + "', vehicle='" + vehicle + "', updateAt='" + updateAt + "', inspection='" + inspection
				+ "', issue='" + issue + "}";
	}


}
