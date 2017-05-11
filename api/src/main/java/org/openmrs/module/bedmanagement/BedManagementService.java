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
package org.openmrs.module.bedmanagement;


import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BedManagementService extends OpenmrsService {

    @Authorized({"Get Admission Locations"})
    List<AdmissionLocation> getAllAdmissionLocations();

    @Authorized({"Get Admission Locations"})
    AdmissionLocation getLayoutForWard(Location location);

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
