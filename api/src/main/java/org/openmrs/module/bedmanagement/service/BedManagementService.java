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
package org.openmrs.module.bedmanagement.service;


import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.AdmissionLocation;
import org.openmrs.module.bedmanagement.BedDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BedManagementService extends OpenmrsService {

    /**
     * Get all admission locations
     *
     * @return {@link List<AdmissionLocation>}
     */
    @Authorized({"Get Admission Locations", "Get Beds"})
    List<AdmissionLocation> getAdmissionLocations();

    /**
     * Get bed location mapping by location
     *
     * @param location {@link Location}
     * @return {@link List< BedLocationMapping >}
     */
    @Authorized({"Get Admission Locations", "Get Beds"})
    List<BedLocationMapping> getBedLocationMappingByLocation(Location location);

    /**
     * Get admission location by location
     *
     * @param location {@link Location}
     * @return
     */
    @Authorized({"Get Admission Locations", "Get Beds"})
    AdmissionLocation getAdmissionLocationByLocation(Location location);

    /**
     * Save / Update admission location
     *
     * @param admissionLocation
     * @return
     */
    @Authorized({"Edit Admission Locations", "Manage Locations"})
    AdmissionLocation saveAdmissionLocation(AdmissionLocation admissionLocation);

    /**
     * Set bed location mapping for admission location
     *
     * @param admissionLocation {@link AdmissionLocation}
     * @param row {@link Integer} admission location bed layout row
     * @param column {@link Integer} admission location bed layout column
     * @return {@link AdmissionLocation}
     */
    @Authorized({"Edit Admission Locations", "Manage Locations"})
    AdmissionLocation setBedLayoutForAdmissionLocation(AdmissionLocation admissionLocation, Integer row, Integer column);

    @Authorized(value = {"Assign Beds", "Edit Admission Locations"}, requireAll=true)
    BedDetails assignPatientToBed(Patient patient, Encounter encounter, String bedId);

    Bed getBedById(int id);

    @Authorized(value = {"Get Beds", "Get Admission Locations"}, requireAll=true)
    BedDetails getBedAssignmentDetailsByPatient(Patient patient);

    @Authorized(value = {"Get Admission Locations","Get Beds"}, requireAll=true)
    BedDetails getBedDetailsById(String id);

    @Authorized(value = {"Get Admission Locations","Get Beds"}, requireAll=true)
    BedDetails getBedDetailsByUuid(String uuid);

    @Authorized(value = {"Get Admission Locations","Get Beds"}, requireAll=true)
    BedPatientAssignment getBedPatientAssignmentByUuid(String uuid);

    @Authorized(value = {"Assign Beds", "Edit Admission Locations"}, requireAll=true)
    BedDetails unAssignPatientFromBed(Patient patient);

    @Authorized(value = {"Get Beds", "Get Admission Locations"}, requireAll=true)
    BedDetails getLatestBedDetailsByVisit(String visitUuid);

    @Authorized(value = {"Get Tags"}, requireAll=true)
    List<BedTag> getAllBedTags();
}
