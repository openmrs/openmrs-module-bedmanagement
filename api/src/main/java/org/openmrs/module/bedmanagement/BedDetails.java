package org.openmrs.module.bedmanagement;

import org.openmrs.Location;
import org.openmrs.Patient;

public class BedDetails {
    private Location physicalLocation;
    private int bedId;
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
        return bedId;
    }

    public void setBedId(int bedId) {
        this.bedId = bedId;
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
                ", bedId=" + bedId +
                ", bedNumber='" + bedNumber + '\'' +
                ", patient=" + patient +
                ", bedType='" + bedType + '\'' +
                '}';
    }

}
