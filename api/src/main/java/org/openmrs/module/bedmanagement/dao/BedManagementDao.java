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
package org.openmrs.module.bedmanagement.dao;


import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.bedmanagement.BedLayout;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.AdmissionLocation;
import org.openmrs.module.bedmanagement.BedDetails;

import java.util.List;

public interface BedManagementDao {
    
    List<AdmissionLocation> getAdmissionLocationsBy(String locationTagName);

    AdmissionLocation getLayoutForWard(Location location);

    BedDetails assignPatientToBed(Patient patient, Encounter encounter, Bed bed);

    Bed getBedById(int id);

    Bed getBedByUuid(String uuid);

    Bed getBedByPatient(Patient patient);

    Location getWardForBed(Bed bed);

    BedDetails unassignPatient(Patient patient, Bed bed);
    
    BedPatientAssignment getBedPatientAssignmentByUuid(String uuid);

    List<BedPatientAssignment> getCurrentAssignmentsByBed(Bed bed);

    Bed getLatestBedByVisit(String visitUuid);

    List<BedTag> getAllBedTags();

    /**
     * Get all admission locations
     *
     * @return {@link List<AdmissionLocation>}
     */
    List<AdmissionLocation> getAdmissionLocations();

    /**
     * Get admission location by location
     *
     * @param location {@link Location}
     * @return {@link AdmissionLocation} return null if not exist
     */
    AdmissionLocation getAdmissionLocationsByLocation(Location location);

    /**
     * Get admissionLocation Ids
     * @return {@link List<Integer>}
     */
    List<Integer> getAdmissionLocationIds();

    /**
     * Get bed location mappings by location
     *
     * @param location {@link Location}
     * @return {@link List< BedLocationMapping >}
     */
    List<BedLocationMapping> getBedLocationMappingByLocation(Location location);

    /**
     * Get bed location mapping by location and row and column
     *
     * @param location {@link Location}  ward's room location
     * @param row {@link Integer} bed row
     * @param column {@link Integer} bed column
     * @return {@link BedLocationMapping}
     */
    BedLocationMapping getBedLocationMappingByLocationAndRowAndColumn(Location location, Integer row, Integer column);

    /**
     * Save / Update Bed location mapping
     *
     * @param bedLocationMapping {@link BedLocationMapping}
     * @return {@link BedLocationMapping}
     */
    BedLocationMapping saveBedLocationMapping(BedLocationMapping bedLocationMapping);

    /**
     * Get bed layout by location
     *
     * @param location {@link Location}
     * @return {@link List<BedLayout>}
     */
    List<BedLayout> getBedLayoutByLocation(Location location);

    /**
     * Get bed location mapping {@link BedLocationMapping} by bed
     *
     * @param bed {@link Bed} bed
     * @return {@link BedLocationMapping}
     */
    BedLocationMapping getBedLocationMappingByBed(Bed bed);
}
