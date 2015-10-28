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

    @Authorized({"Assign Beds", "Edit Admission Locations"})
    BedDetails assignPatientToBed(Patient patient, Encounter encounter, String bedId);

    Bed getBedById(int id);

    @Authorized({"Get Admission Locations"})
    BedDetails getBedAssignmentDetailsByPatient(Patient patient);

    @Authorized({"Get Admission Locations"})
    BedDetails getBedDetailsById(String id);

    @Authorized({"Get Admission Locations"})
    BedDetails getBedDetailsByUuid(String uuid);

    @Authorized({"Get Admission Locations"})
    BedPatientAssignment getBedPatientAssignmentByUuid(String uuid);

    @Authorized({"Assign Beds", "Edit Admission Locations"})
    BedDetails unAssignPatientFromBed(Patient patient);
    
}
