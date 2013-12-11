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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public BedDetails assignPatientToBed(Patient patient, Encounter encounter, String bedId) {
        BedDetails prev = this.unAssignPatientFromBed(patient);
        Bed bed = dao.getBedById(Integer.parseInt(bedId));
        BedDetails current = dao.assignPatientToBed(patient, encounter, bed);
        BedPatientAssignment prevAssignment = (prev != null) ? prev.getLastAssignment() : null;
        current.setLastAssignment(prevAssignment);
        return current;
    }

    @Override
    public Bed getBedById(int id) {
        return dao.getBedById(id);
    }


    @Override
    public BedDetails getBedAssignmentDetailsByPatient(Patient patient) {
        Bed bed = dao.getBedByPatient(patient);
        if (bed != null) {
            BedPatientAssignment currentAssignment = dao.getCurrentAssignmentByBed(bed);
            Location physicalLocation = dao.getWardForBed(bed);
            return constructBedDetails(bed, physicalLocation, currentAssignment);
        }
        return null;
    }

    @Override
    public BedDetails getBedDetailsById(String id) {
        Bed bed = dao.getBedById(Integer.parseInt(id));
        if (bed != null) {
            BedPatientAssignment currentAssignment = dao.getCurrentAssignmentByBed(bed);
            Location location = dao.getWardForBed(bed);
            BedDetails bedDetails = constructBedDetails(bed, location, currentAssignment);
            return bedDetails;
        }
        return null;
    }

    @Override
    public BedDetails getBedDetailsByUuid(String uuid) {
        Bed bed = dao.getBedByUuid(uuid);
        if (bed != null) {
            BedPatientAssignment currentAssignment = dao.getCurrentAssignmentByBed(bed);
            Location location = dao.getWardForBed(bed);
            BedDetails bedDetails = constructBedDetails(bed, location, currentAssignment);
            return bedDetails;
        }
        return null;
    }

    @Override
    public BedPatientAssignment getBedPatientAssignmentByUuid(String uuid) {
        return dao.getBedPatientAssignmentByUuid(uuid);
    }

    @Override
    @Transactional
    public BedDetails unAssignPatientFromBed(Patient patient) {
        Bed currentBed = dao.getBedByPatient(patient);
        if (currentBed != null) {
            return dao.unassignPatient(patient, currentBed);
        }
        return null;
    }

    private BedDetails constructBedDetails(Bed bed, Location location, BedPatientAssignment currentAssignment) {
        BedDetails bedDetails = new BedDetails();
        bedDetails.setBed(bed);
        bedDetails.setBedNumber(bed.getBedNumber());
        if (currentAssignment != null) {
            bedDetails.setPatient(currentAssignment.getPatient());
            bedDetails.setCurrentAssignment(currentAssignment);
        }
        bedDetails.setPhysicalLocation(location);
        bedDetails.setBedType(bed.getBedType());
        return bedDetails;
    }

}
