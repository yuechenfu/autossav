package com.hiveel.autossav.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;
import com.hiveel.core.util.DateUtil;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Issue implements Serializable, NotNullObject {
    private Long id;
    private Vehicle vehicle;
    private Person driver;
    private Problem problem;
    private String name;
    private String content;
    private String apptMinDate;
    private String apptMaxDate;
    private IssueStatus status;
    private Integer odometer;
    private Double lon;
    private Double lat;
    private Double tax;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static final Null NULL = new Null();

    public void fillNotRequire() {
    	name = name != null ? name : "";
        content = content != null ? content : "";
        apptMinDate = apptMinDate != null ? apptMinDate : DateUtil.newUtcTimeInstance();
        apptMaxDate = apptMaxDate != null ? apptMaxDate : "";
        lon = lon != null ? lon : 0.0;
        lat = lat != null ? lat : 0.0;
        tax = tax != null ? tax : 0.1;
    }

    public void createAt() {
        createAt =  LocalDateTime.now(ZoneId.of("UTC"));
    }
    public void updateAt() {
        updateAt =  LocalDateTime.now(ZoneId.of("UTC"));
    }
    private static class Null extends Issue implements NullObject {
        @Override
        public Vehicle getVehicle() { return Vehicle.NULL; }
        
        @Override
        public Person getDriver() { return Person.NULL; }
        
        @Override
        public Problem getProblem() { return Problem.NULL; }
    }
    public static class Builder extends AbstractBuilder {

        @Override
        public Issue build() {
            try {
                return super.build(Issue.class);
            } catch (InstantiationException | IllegalAccessException e) {
                return NULL;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getOdometer() {
        return odometer;
    }

    public void setOdometer(Integer odometer) {
        this.odometer = odometer;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
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

    public String getApptMinDate() {
		return apptMinDate;
	}

	public void setApptMinDate(String apptMinDate) {
		this.apptMinDate = apptMinDate;
	}

	public String getApptMaxDate() {
		return apptMaxDate;
	}

	public void setApptMaxDate(String apptMaxDate) {
		this.apptMaxDate = apptMaxDate;
	}

	public Problem getProblem() {
		return problem;
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}

	@Override
	public String toString() {
		return "Issue {id='" + id + "', vehicle='" + vehicle + "', driver='" + driver + "', problem='" + problem
				+ "', name='" + name + "', content='" + content + "', apptMinDate='" + apptMinDate + "', apptMaxDate='"
				+ apptMaxDate + "', status='" + status + "', odometer='" + odometer + "', lon='" + lon + "', lat='"
				+ lat + "', tax='" + tax + "', createAt='" + createAt + "', updateAt='" + updateAt + "}";
	}
}
