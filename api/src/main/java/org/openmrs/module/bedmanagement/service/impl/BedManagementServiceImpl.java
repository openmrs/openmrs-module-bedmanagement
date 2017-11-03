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
package org.openmrs.module.bedmanagement.service.impl;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.bedmanagement.constants.BedManagementApiConstants;
import org.openmrs.module.bedmanagement.dao.BedManagementDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.AdmissionLocation;
import org.openmrs.module.bedmanagement.BedDetails;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class BedManagementServiceImpl extends BaseOpenmrsService implements BedManagementService {

    BedManagementDao bedManagementDao;

    public void setDao(BedManagementDao dao) {
        this.bedManagementDao = dao;
    }

    @Override
    public List<AdmissionLocation> getAllAdmissionLocations() {
        return bedManagementDao.getAdmissionLocationsBy(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION);
    }

    @Override
    public AdmissionLocation getLayoutForWard(Location location) {
        return bedManagementDao.getLayoutForWard(location);
    }

    @Override
    @Transactional
    public BedDetails assignPatientToBed(Patient patient, Encounter encounter, String bedId) {
        BedDetails prev = this.unAssignPatientFromBed(patient);
        Bed bed = bedManagementDao.getBedById(Integer.parseInt(bedId));
        BedDetails current = bedManagementDao.assignPatientToBed(patient, encounter, bed);
        BedPatientAssignment prevAssignment = (prev != null) ? prev.getLastAssignment() : null;
        current.setLastAssignment(prevAssignment);
        return current;
    }

    @Override
    public Bed getBedById(int id) {
        return bedManagementDao.getBedById(id);
    }


    @Override
    public BedDetails getBedAssignmentDetailsByPatient(Patient patient) {
        Bed bed = bedManagementDao.getBedByPatient(patient);
        if (bed != null) {
            List<BedPatientAssignment> currentAssignments = bedManagementDao.getCurrentAssignmentsByBed(bed);
            Location physicalLocation = bedManagementDao.getWardForBed(bed);
            return constructBedDetails(bed, physicalLocation, currentAssignments);
        }
        return null;
    }

    @Override
    public BedDetails getBedDetailsById(String id) {
        Bed bed = bedManagementDao.getBedById(Integer.parseInt(id));
        if (bed != null) {
            List<BedPatientAssignment> currentAssignments = bedManagementDao.getCurrentAssignmentsByBed(bed);
            Location location = bedManagementDao.getWardForBed(bed);
            BedDetails bedDetails = constructBedDetails(bed, location, currentAssignments);
            return bedDetails;
        }
        return null;
    }

    @Override
    public BedDetails getBedDetailsByUuid(String uuid) {
        Bed bed = bedManagementDao.getBedByUuid(uuid);
        if (bed != null) {
            List<BedPatientAssignment> currentAssignment = bedManagementDao.getCurrentAssignmentsByBed(bed);
            Location location = bedManagementDao.getWardForBed(bed);
            BedDetails bedDetails = constructBedDetails(bed, location, currentAssignment);
            return bedDetails;
        }
        return null;
    }

    @Override
    public BedPatientAssignment getBedPatientAssignmentByUuid(String uuid) {
        return bedManagementDao.getBedPatientAssignmentByUuid(uuid);
    }

    @Override
    @Transactional
    public BedDetails unAssignPatientFromBed(Patient patient) {
        Bed currentBed = bedManagementDao.getBedByPatient(patient);
        if (currentBed != null) {
            return bedManagementDao.unassignPatient(patient, currentBed);
        }
        return null;
    }

    @Override
    @Transactional
    public BedDetails getLatestBedDetailsByVisit(String visitUuid) {
        Bed bed = bedManagementDao.getLatestBedByVisit(visitUuid);
        if (bed != null) {
            Location physicalLocation = bedManagementDao.getWardForBed(bed);
            return constructBedDetails(bed, physicalLocation, new ArrayList<BedPatientAssignment>());
        }
        return null;
    }

    @Override
    public List<BedTag> getAllBedTags() {
        return  bedManagementDao.getAllBedTags();
    }

    private BedDetails constructBedDetails(Bed bed, Location location, List<BedPatientAssignment> currentAssignments) {
        BedDetails bedDetails = new BedDetails();
        bedDetails.setBed(bed);
        bedDetails.setBedNumber(bed.getBedNumber());
        List<Patient> patients = new ArrayList<Patient>();
        for (BedPatientAssignment assignment : currentAssignments) {
            patients.add(assignment.getPatient());
        }
        bedDetails.setPatients(patients);
        bedDetails.setCurrentAssignments(currentAssignments);
        bedDetails.setPhysicalLocation(location);
        bedDetails.setBedType(bed.getBedType());
        return bedDetails;
    }

}
