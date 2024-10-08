package org.openmrs.module.bedmanagement;

import org.openmrs.Patient;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.entity.BedTagMap;
import org.openmrs.module.bedmanagement.entity.BedType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BedLayout {
	
	private Integer rowNumber;
	
	private Integer columnNumber;
	
	private String bedNumber;
	
	private Integer bedId;
	
	private String bedUuid;
	
	private String status;
	
	private BedType bedType;
	
	private String location;
	
	private List<BedPatientAssignment> bedPatientAssignments;
	
	private Set<BedTagMap> bedTagMaps;
	
	public String getBedNumber() {
		return bedNumber;
	}
	
	public void setBedNumber(String bedNumber) {
		this.bedNumber = bedNumber;
	}
	
	public Integer getBedId() {
		return bedId;
	}
	
	public void setBedId(Integer bedId) {
		this.bedId = bedId;
	}
	
	public String getBedUuid() {
		return bedUuid;
	}
	
	public void setBedUuid(String bedUuid) {
		this.bedUuid = bedUuid;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Integer getRowNumber() {
		return rowNumber;
	}
	
	public void setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
	}
	
	public Integer getColumnNumber() {
		return columnNumber;
	}
	
	public void setColumnNumber(Integer columnNumber) {
		this.columnNumber = columnNumber;
	}
	
	public BedType getBedType() {
		return bedType;
	}
	
	public void setBedType(BedType bedType) {
		this.bedType = bedType;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public List<BedPatientAssignment> getBedPatientAssignments() {
		return bedPatientAssignments;
	}
	
	public void setBedPatientAssignments(List<BedPatientAssignment> bedPatientAssignments) {
		this.bedPatientAssignments = bedPatientAssignments;
	}
	
	public Set<Patient> getPatients() {
		Set<Patient> patients = new HashSet<>();
		if (bedPatientAssignments != null) {
			for (BedPatientAssignment assignment : bedPatientAssignments) {
				patients.add(assignment.getPatient());
			}
		}
		return patients;
	}
	
	@Override
	public String toString() {
		return "BedLayout{" + "rowNumber=" + rowNumber + ", columnNumber=" + columnNumber + ", bedNumber='" + bedNumber
		        + '\'' + ", bedId=" + bedId + ", bedUuid=" + bedUuid + ", status='" + status + '\'' + ", bedType=" + bedType
		        + ", location='" + location + '\'' + '}';
	}
	
	public Set<BedTagMap> getBedTagMaps() {
		return bedTagMaps;
	}
	
	public void setBedTagMaps(Set<BedTagMap> bedTagMaps) {
		this.bedTagMaps = bedTagMaps;
	}
}
