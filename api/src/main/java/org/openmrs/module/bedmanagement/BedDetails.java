package org.openmrs.module.bedmanagement;

import org.openmrs.Location;
import org.openmrs.Patient;

import java.util.ArrayList;
import java.util.List;

public class BedDetails {
    private Location physicalLocation;
    private Bed bed;
    private String bedNumber;
    private List<Patient> patients;
    private BedType bedType;
    private List<BedPatientAssignment> currentAssignments;
    private BedPatientAssignment lastAssignment;

    public Location getPhysicalLocation() {
        return physicalLocation;
    }

    public void setPhysicalLocation(Location physicalLocation) {
        this.physicalLocation = physicalLocation;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public int getBedId() {
        return bed.getId();
    }

    public String getBedUuid() {
        return bed.getUuid();
    }

    public BedType getBedType() {
        return bedType;
    }

    public void setBedType(BedType bedType) {
        this.bedType = bedType;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    @Override
    public String toString() {
        return "BedDetails{" +
                "physicalLocation=" + physicalLocation +
                ", bedId=" + getBedId() +
                ", bedNumber='" + bedNumber + '\'' +
                ", patients=" + patients +
                ", bedType='" + bedType + '\'' +
                '}';
    }

    public Bed getBed() {
        return bed;
    }

    public void setBed(Bed bed) {
        this.bed = bed;
    }

    public List<BedPatientAssignment> getCurrentAssignments() {
        return currentAssignments;
    }

    public void setCurrentAssignments(List<BedPatientAssignment> currentAssignments) {
        this.currentAssignments = currentAssignments;
    }

    public void setLastAssignment(BedPatientAssignment lastAssignment) {
        this.lastAssignment = lastAssignment;
    }

    public BedPatientAssignment getLastAssignment() {
        return lastAssignment;
    }

    public void addCurrentAssignment(BedPatientAssignment bedPatientAssignment){
        if(this.currentAssignments == null){
            this.currentAssignments = new ArrayList<BedPatientAssignment>();
        }
        this.currentAssignments.add(bedPatientAssignment);
    }
}
