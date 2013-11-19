package org.openmrs.module.bedmanagement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
@JsonIgnoreProperties({"patient"})
public class BedLayout {
    private Integer rowNumber;
    private Integer columnNumber;
    private String bedNumber;
    private Integer bedId;
    private String status;
    private BedType bedType;

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

    @Override
    public String toString() {
        return "BedLayout{" +
                "rowNumber=" + rowNumber +
                ", columnNumber=" + columnNumber +
                ", bedNumber='" + bedNumber + '\'' +
                ", bedId=" + bedId +
                ", status='" + status + '\'' +
                ", bedType=" + bedType +
                '}';
    }
}
