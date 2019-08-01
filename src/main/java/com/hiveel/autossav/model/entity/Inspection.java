package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Inspection implements Serializable, NotNullObject {
    private Long id;
    private Vehicle vehicle;
    private Person driver;
    private Person autosave;
    private Address address;
    private String name;
    private String content;
    private InspectionStatus status;
    private String date;  
    private Integer odometer;
    private Double tax;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    
    public void fillNotRequire() {
    	content = content != null ? content : "";
    	tax = tax != null ? tax : 0.1;
    }

    public void createAt() {
        createAt =LocalDateTime.now(ZoneId.of("UTC"));
    }
    public void updateAt() {
        updateAt = LocalDateTime.now(ZoneId.of("UTC"));
    }
	
    public static final Null NULL = new Null();
    private static class Null extends Inspection implements NullObject {
        @Override
        public Vehicle getVehicle() { return Vehicle.NULL; }       	
        @Override
        public Person getDriver() { return Person.NULL; }    
        @Override
        public Person getAutosave() { return Person.NULL; } 
        @Override
        public Address getAddress() { return Address.NULL; }
    }
    public static class Builder extends AbstractBuilder {
        @Override
        public Inspection build() {
            try {
                return super.build(Inspection.class);
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
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Integer getOdometer() {
		return odometer;
	}
	public void setOdometer(Integer odometer) {
		this.odometer = odometer;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public InspectionStatus getStatus() {
		return status;
	}
	public void setStatus(InspectionStatus status) {
		this.status = status;
	}
	public Double getTax() {
		return tax;
	}
	public void setTax(Double tax) {
		this.tax = tax;
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

	public Person getAutosave() {
		return autosave;
	}

	public void setAutosave(Person autosave) {
		this.autosave = autosave;
	}
	
    public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Inspection {id='" + id + "', vehicle='" + vehicle + "', driver='" + driver + "', autosave='" + autosave
				+ "', address='" + address + "', name='" + name + "', content='" + content + "', status='" + status
				+ "', date='" + date + "', odometer='" + odometer + "', tax='" + tax + "', createAt='" + createAt
				+ "', updateAt='" + updateAt + "}";
	}
}
