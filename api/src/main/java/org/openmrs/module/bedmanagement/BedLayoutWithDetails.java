package org.openmrs.module.bedmanagement;

import java.util.HashSet;
import java.util.Set;

public class BedLayoutWithDetails {
    private Integer rowNumber;
    private Integer columnNumber;
    private String location;
    private Bed bed;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Bed getBed() {
        return bed;
    }

    public void setBed(Bed bed) {
        this.bed = bed;
    }

    public BedLayout convertToBedLayout() {
        BedLayout bedLayout = new BedLayout();
        bedLayout.setRowNumber(this.rowNumber);
        bedLayout.setColumnNumber(this.columnNumber);
        bedLayout.setLocation(this.location);
        bedLayout.setBedNumber(this.bed.getBedNumber());
        bedLayout.setBedId(this.bed.getId());
        bedLayout.setStatus(this.bed.getStatus());
        bedLayout.setBedType(this.bed.getBedType());
        bedLayout.setBedTagMaps(this.bed.getBedTagMap());
        setPatientInfo(bedLayout);
        return bedLayout;
    }

    private void setPatientInfo(BedLayout bedLayout) {
        Set<BedPatientAssignment> bedPatientAssignment = this.bed.getBedPatientAssignment();
        for (BedPatientAssignment patientAssignment : bedPatientAssignment) {
            if (patientAssignment.getEndDatetime() == null) {
                bedLayout.setPatient(patientAssignment.getPatient());
            }
        }
    }
}
