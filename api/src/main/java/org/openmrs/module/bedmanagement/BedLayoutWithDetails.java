package org.openmrs.module.bedmanagement;

import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;

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
        bedLayout.setBedNumber(this.bed != null ? this.bed.getBedNumber() : null);
        bedLayout.setBedId(this.bed != null ? this.bed.getId() : null);
        bedLayout.setBedUuid(this.bed != null ? this.bed.getUuid() : null);
        bedLayout.setStatus(this.bed != null ? this.bed.getStatus() : null);
        bedLayout.setBedType(this.bed != null ? this.bed.getBedType() : null);
        bedLayout.setBedTagMaps(this.bed != null ? this.bed.getBedTagMap() : null);
        if (this.bed != null)
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
