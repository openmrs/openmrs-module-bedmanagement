package org.openmrs.module.bedmanagement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openmrs.Patient;
@JsonIgnoreProperties({"patient"})
public class BedLayout {
    private Integer rowNumber;
    private Integer columnNumber;
    private String bedNumber;
    private Integer bedId;
    private String status;
    private String patientGender;
    private String patientIdentifier;
    private String patientName;
    private Patient patient;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        if(patient == null){
            return;
        }
        this.patient = patient;
        setPatientGender(patient.getGender());
        setPatientIdentifier(patient.getPatientIdentifier().getIdentifier());
        setPatientName(patient.getPersonName().getFullName());
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientIdentifier() {
        return patientIdentifier;
    }

    public void setPatientIdentifier(String patientIdentifier) {
        this.patientIdentifier = patientIdentifier;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

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

    @Override
    public String toString() {
        return "BedLayout{" +
                "rowNumber=" + rowNumber +
                ", columnNumber=" + columnNumber +
                ", bedNumber='" + bedNumber + '\'' +
                ", bedId=" + bedId +
                ", status='" + status + '\'' +
                '}';
    }
}
