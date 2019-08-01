package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Plan implements Serializable, NotNullObject {
    private Long id;
    private Vehicle vehicle;
    private Address address;
    private Integer day;
    private LocalDateTime updateAt;

    public void updateAt() {
        updateAt =  LocalDateTime.now(ZoneId.of("UTC"));
    }
    
    public static final Null NULL = new Null();
    private static class Null extends Plan implements NullObject {
        @Override
        public Vehicle getVehicle() { return Vehicle.NULL; }
        @Override
        public Address getAddress() { return Address.NULL; }
        
    }
    public static class Builder extends AbstractBuilder {
        @Override
        public Plan build() {
            try {
                return super.build(Plan.class);
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

    public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Override
	public String toString() {
		return "Plan {id='" + id + "', vehicle='" + vehicle + "', address='" + address + "', day='" + day
				+ "', updateAt='" + updateAt + "}";
	}
}
