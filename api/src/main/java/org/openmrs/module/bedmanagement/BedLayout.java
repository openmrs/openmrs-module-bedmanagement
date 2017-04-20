package org.openmrs.module.bedmanagement;

import org.openmrs.Patient;

import java.util.Set;

public class BedLayout {
    private Integer rowNumber;
    private Integer columnNumber;
    private String bedNumber;
    private Integer bedId;
    private String status;
    private BedType bedType;
    private String location;
    private Patient patient;
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "BedLayout{" +
                "rowNumber=" + rowNumber +
                ", columnNumber=" + columnNumber +
                ", bedNumber='" + bedNumber + '\'' +
                ", bedId=" + bedId +
                ", status='" + status + '\'' +
                ", bedType=" + bedType +
                ", location='" + location + '\'' +
                '}';
    }

    public Set<BedTagMap> getBedTagMaps() {
        return bedTagMaps;
    }

    public void setBedTagMaps(Set<BedTagMap> bedTagMaps) {
        this.bedTagMaps = bedTagMaps;
    }
}
