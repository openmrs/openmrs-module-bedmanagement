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
import org.openmrs.api.impl.BaseOpenmrsService;

import java.util.Iterator;
import java.util.List;
    
public class BedManagementServiceImpl extends BaseOpenmrsService implements BedManagementService {

    BedManagementDAO dao;
    
    public void setDao(BedManagementDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<AdmissionLocation> getAllAdmissionLocations() {
        return dao.getAdmissionLocationsBy(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION);
    }

    @Override
    public AdmissionLocation getLayoutForWard(Location location) {
        return dao.getLayoutForWard(location);
    }

    @Override
    public BedDetails assignPatientToBed(Patient patient, Encounter encounter, String bedId) {
        Bed currentBed = dao.getBedByPatient(patient);
        if (currentBed != null) {
            dao.unassignPatient(patient, currentBed);
        }
        Bed bed = dao.getBedById(Integer.parseInt(bedId));
        return dao.assignPatientToBed(patient, encounter, bed);
    }

    @Override
    public void freeBed(Patient patient) {
        Bed currentBed = dao.getBedByPatient(patient);
        if (currentBed != null) {
            dao.unassignPatient(patient, currentBed);
        }
    }

    @Override
    public Bed getBedById(int id) {
        return dao.getBedById(id);
    }


    @Override
    public BedDetails getBedAssignmentDetailsByPatient(Patient patient) {
        Bed bed = dao.getBedByPatient(patient);
        if (bed != null) {
            Location physicalLocation = dao.getWardForBed(bed);
            return constructBedDetails(bed, patient, physicalLocation);
        }
        return null;
    }

    @Override
    public BedDetails getBedDetailsById(String id) {
        Bed bed = dao.getBedById(Integer.parseInt(id));
        if (bed != null) {
            Patient assignedPatient = getAssignedPatient(bed);
            Location location = dao.getWardForBed(bed);
            BedDetails bedDetails = constructBedDetails(bed, assignedPatient, location);
            return bedDetails;
        }
        return null;
    }

    @Override
    public BedDetails getBedDetailsByUuid(String uuid) {
        Bed bed = dao.getBedByUuid(uuid);
        if (bed != null) {
            Patient assignedPatient = getAssignedPatient(bed);
            Location location = dao.getWardForBed(bed);
            BedDetails bedDetails = constructBedDetails(bed, assignedPatient, location);
            return bedDetails;
        }
        return null;
    }

    @Override
    public BedPatientAssignment getBedPatientAssignmentByUuid(String uuid) {
        return dao.getBedPatientAssignmentByUuid(uuid);
    }

    private BedDetails constructBedDetails(Bed bed, Patient patient, Location location) {
        BedDetails bedDetails = new BedDetails();
        bedDetails.setBed(bed);
        bedDetails.setBedNumber(bed.getBedNumber());
        bedDetails.setPatient(patient);
        bedDetails.setPhysicalLocation(location);
        bedDetails.setBedType(bed.getBedType());
        return bedDetails;
    }

    private Patient getAssignedPatient(Bed bed) {
        Iterator<BedPatientAssignment> iterator = bed.getBedPatientAssignment().iterator();
        while (iterator.hasNext()) {
            BedPatientAssignment bedPatientAssignment = iterator.next();
            if (bedPatientAssignment.getEndDatetime() == null) {
                return bedPatientAssignment.getPatient();
            }
        }
        return null;
    }
}
