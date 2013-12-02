package org.openmrs.module.bedmanagement;

import org.openmrs.Location;
import org.openmrs.Patient;

public class BedDetails {
    private Location physicalLocation;
    private Bed bed;
    private String bedNumber;
    private Patient patient;
    private BedType bedType;

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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "BedDetails{" +
                "physicalLocation=" + physicalLocation +
                ", bedId=" + getBedId() +
                ", bedNumber='" + bedNumber + '\'' +
                ", patient=" + patient +
                ", bedType='" + bedType + '\'' +
                '}';
    }

    public Bed getBed() {
        return bed;
    }

    public void setBed(Bed bed) {
        this.bed = bed;
    }
}
