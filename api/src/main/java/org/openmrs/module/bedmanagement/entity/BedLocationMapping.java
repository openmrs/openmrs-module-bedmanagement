package org.openmrs.module.bedmanagement.entity;

import org.openmrs.Location;

/**
 * Maps to bed_location_mapping table in openmrs database
 */
public class BedLocationMapping {
    private int id;
    private Location location;
    private Bed bed;
    private int row;
    private int column;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Bed getBed() {
        return bed;
    }

    public void setBed(Bed bed) {
        this.bed = bed;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return "BedLocationMapping{" +
                "id=" + id +
                ", locationId=" + (location != null ? location.getId() : null) +
                ", bedId=" + (bed != null ? bed.getId() : null) +
                ", row=" + row +
                ", column=" + column +
                '}';
    }
}
