package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class VehicleDriverRelate implements Serializable, NotNullObject {
    private Long id;
    private Vehicle vehicle;
    private Person driver;
    private String onDate;
    private String offDate;
    private String onOdometer;
    private String offOdometer;
    private LocalDateTime updateAt;
    
	public void updateAt() {
		updateAt =  LocalDateTime.now(ZoneId.of("UTC"));
	}
	
    public static final Null NULL = new Null();
    private static class Null extends VehicleDriverRelate implements NullObject {
        @Override
        public Vehicle getVehicle() { return Vehicle.NULL; }       	
        @Override
        public Person getDriver() { return Person.NULL; }           
    }
    public static class Builder extends AbstractBuilder {
        @Override
        public VehicleDriverRelate build() {
            try {
                return super.build(VehicleDriverRelate.class);
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
	public Person getDriver() {
		return driver;
	}
	public void setDriver(Person driver) {
		this.driver = driver;
	}
	public String getOnDate() {
		return onDate;
	}
	public void setOnDate(String onDate) {
		this.onDate = onDate;
	}
	public String getOffDate() {
		return offDate;
	}
	public void setOffDate(String offDate) {
		this.offDate = offDate;
	}

	public LocalDateTime getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(LocalDateTime updateAt) {
		this.updateAt = updateAt;
	}

	public String getOnOdometer() {
		return onOdometer;
	}
	public void setOnOdometer(String onOdometer) {
		this.onOdometer = onOdometer;
	}
	public String getOffOdometer() {
		return offOdometer;
	}
	public void setOffOdometer(String offOdometer) {
		this.offOdometer = offOdometer;
	}
	@Override
	public String toString() {
		return "VehicleDriverRelate {id='" + id + "', vehicle='" + vehicle + "', driver='" + driver + "', onDate='"
				+ onDate + "', offDate='" + offDate + "', onOdometer='" + onOdometer + "', offOdometer='" + offOdometer
				+ "', updateAt='" + updateAt + "}";
	}

}
