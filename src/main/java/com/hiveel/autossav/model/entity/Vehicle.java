package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Vehicle implements Serializable, NotNullObject {
    private Long id;
    private VehicleGroup group;
    private String name;
    private String year;
    private String make;
    private String model;
    private VehicleStatus status;
    private VehicleType type;
    private String vin;
    private String plate;
    private Boolean rental;
    private String imgsrc;
    private Integer odometer;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    private Person driver;

	public void fillNotRequire() {
		vin = vin != null ? vin : "";
		name = name != null ? name : "";
		group = group != null ? group : new VehicleGroup.Builder().set("id", 0L).build();
		plate = plate != null ? plate : "";
		rental = rental != null ? rental : false;
		imgsrc = imgsrc != null ? imgsrc : "";
		odometer = odometer != null ? odometer : 0;
		type = type != null ? type : VehicleType.VE;
	}

	public void createAt() {
		createAt =  LocalDateTime.now(ZoneId.of("UTC"));
	}
	public void updateAt() {
		updateAt =  LocalDateTime.now(ZoneId.of("UTC"));
	}

    public static final Null NULL = new Null();
    private static class Null extends Vehicle implements NullObject {
        @Override
        public VehicleGroup getGroup() { return VehicleGroup.NULL; }
        @Override
        public Person getDriver() { return Person.NULL; }
    }
    public static class Builder extends AbstractBuilder {
        @Override
        public Vehicle build() {
            try {
                return super.build(Vehicle.class);
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

	public VehicleGroup getGroup() {
		return group;
	}

	public void setGroup(VehicleGroup group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setName(String name) {
		this.name = name;
	}

	public VehicleStatus getStatus() {
		return status;
	}

	public void setStatus(VehicleStatus status) {
		this.status = status;
	}

	public VehicleType getType() {
		return type;
	}

	public void setType(VehicleType type) {
		this.type = type;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	public Boolean getRental() {
		return rental;
	}

	public void setRental(Boolean rental) {
		this.rental = rental;
	}

    public String getImgsrc() {
		return imgsrc;
	}
	public void setImgsrc(String imgsrc) {
		this.imgsrc = imgsrc;
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

	public Integer getOdometer() {
		return odometer;
	}

	public void setOdometer(Integer odometer) {
		this.odometer = odometer;
	}

	public Person getDriver() {
		return driver;
	}

	public void setDriver(Person driver) {
		this.driver = driver;
	}

	@Override
	public String toString() {
		return "Vehicle {id='" + id + "', group='" + group + "', name='" + name + "', status='" + status + "', vin='"
				+ vin + "', plate='" + plate + "', rental='" + rental + "', imgsrc='" + imgsrc + "', createAt='"
				+ createAt + "', updateAt='" + updateAt + "}";
	}
}
