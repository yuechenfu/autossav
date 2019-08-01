package com.hiveel.autossav.model;

import java.util.List;

import com.hiveel.core.model.searchcondition.AbstractSearchCondition;

public class SearchCondition extends AbstractSearchCondition {
    private String status;
    private String type;    
    private String group;
    private Boolean rental;
    private List<String> typeList;
    private List<String> idList;

    // offDate for VehicleDriverRelate
    private String offDate;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getRental() {
		return rental;
	}
	public void setRental(Boolean rental) {
		this.rental = rental;
	}
	public String getOffDate() {
		return offDate;
	}
	public void setOffDate(String offDate) {
		this.offDate = offDate;
	}
	public List<String> getTypeList() {
		return typeList;
	}
	public void setTypeList(List<String> typeList) {
		this.typeList = typeList;
	}
	public List<String> getIdList() {
		return idList;
	}
	public void setIdList(List<String> idList) {
		this.idList = idList;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	

}
