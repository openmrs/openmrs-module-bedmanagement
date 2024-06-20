package org.openmrs.module.bedmanagement.entity;

import org.openmrs.BaseOpenmrsData;

import java.util.Set;

public class Bed extends BaseOpenmrsData {
	
	private Integer id;
	
	private String bedNumber;
	
	private BedType bedType;
	
	private String status;
	
	private Set<BedTagMap> bedTagMap;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getBedNumber() {
		return bedNumber;
	}
	
	public void setBedNumber(String number) {
		this.bedNumber = number;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public BedType getBedType() {
		return bedType;
	}
	
	public void setBedType(BedType bedType) {
		this.bedType = bedType;
	}
	
	public Set<BedTagMap> getBedTagMap() {
		return bedTagMap;
	}
	
	public void setBedTagMap(Set<BedTagMap> bedTagMap) {
		this.bedTagMap = bedTagMap;
	}
	
}
