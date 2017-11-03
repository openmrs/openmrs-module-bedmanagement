/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.bedmanagement.pojo;

import org.openmrs.Location;

import java.util.List;

/**
 * Maps to resource for location table in openmrs database. Essentially
 * a delegate for the AdmissionLocationResource
 */

public class AdmissionLocation {
    private Long totalBeds;
    private Long occupiedBeds;
    private Location ward;
    private List<BedLayout> bedLayouts;

    public Location getWard() {
        return ward;
    }

    public void setWard(Location ward) {
        this.ward = ward;
    }

    public Long getTotalBeds() {
        return totalBeds;
    }

    public Long getOccupiedBeds() {
        return occupiedBeds;
    }

    public void setTotalBeds(Long totalBeds) {
        this.totalBeds = totalBeds;
    }

    public void setOccupiedBeds(Long occupiedBeds) {
        this.occupiedBeds = occupiedBeds;
    }

    public void setBedLayouts(List<BedLayout> bedLayouts) {
        this.bedLayouts = bedLayouts;
    }

    public List<BedLayout> getBedLayouts() {
        return bedLayouts;
    }

    @Override
    public String toString() {
        return "AdmissionLocation{" +
                "totalBeds=" + totalBeds +
                ", occupiedBeds=" + occupiedBeds +
                ", ward=" + ward +
                ", bedLayouts=" + bedLayouts +
                '}';
    }
}
