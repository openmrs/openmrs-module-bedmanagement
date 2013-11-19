package org.openmrs.module.bedmanagement;

import java.util.Set;

public class Bed {
    private int id;
    private String bedNumber;
    private Set<BedPatientAssignment> bedPatientAssignment;
    private String status;
    private BedType bedType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String number) {
        this.bedNumber = number;
    }

    public Set<BedPatientAssignment> getBedPatientAssignment() {
        return bedPatientAssignment;
    }

    public void setBedPatientAssignment(Set<BedPatientAssignment> bedPatientAssignment) {
        this.bedPatientAssignment = bedPatientAssignment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status.toString();
    }

    public BedType getBedType() {
        return bedType;
    }

    public void setBedType(BedType bedType) {
        this.bedType = bedType;
    }
}
