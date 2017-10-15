/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.bedmanagement;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BedManagementServiceImpl extends BaseOpenmrsService implements BedManagementService {

    BedManagementDAO dao;

    BedDAO bedDao;

    public void setDao(BedManagementDAO dao) {
        this.dao = dao;
    }

    public void setBedDao(BedDAO bedDao) {
        this.bedDao = bedDao;
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
        Bed bed = bedDao.getById(Integer.parseInt(bedId));
        BedDetails current = dao.assignPatientToBed(patient, encounter, bed);
        BedPatientAssignment prevAssignment = (prev != null) ? prev.getLastAssignment() : null;
        current.setLastAssignment(prevAssignment);
        return current;
    }

    @Override
    public Bed getBedById(int id) {
        return bedDao.getById(id);
    }

    @Override
    public Bed getBedByUuid(String uuid) {
        return bedDao.getByUuid(uuid);
    }

    @Override
    public List<Bed> listBeds(String bedType, String status, Integer limit, Integer offset) {
        List<Bed> bedList = new ArrayList<>();
        if (bedType != null && status == null) {
            bedList = bedDao.searchByBedType(bedType, limit, offset);
        } else if (bedType == null && status != null) {
            bedList = bedDao.searchByBedStatus(status, limit, offset);
        } else if (bedType != null && status != null) {
            bedList = bedDao.searchByBedTypeAndStatus(bedType, status, limit, offset);
        } else {
            bedList = bedDao.getAll(limit, offset);
        }

        return bedList;
    }

    @Override
    public Bed saveBed(Bed bed) {
        bedDao.save(bed);
        return bed;
    }

    @Override
    public void deleteBed(Bed bed, String reason) {
        bed.setVoided(true);
        bed.setDateVoided(new Date());
        bed.setVoidReason(reason);
        bed.setVoidedBy(Context.getAuthenticatedUser());
        bedDao.save(bed);
    }

    @Override
    public BedDetails getBedAssignmentDetailsByPatient(Patient patient) {
        Bed bed = dao.getBedByPatient(patient);
        if (bed != null) {
            List<BedPatientAssignment> currentAssignments = dao.getCurrentAssignmentsByBed(bed);
            Location physicalLocation = dao.getWardForBed(bed);
            return constructBedDetails(bed, physicalLocation, currentAssignments);
        }
        return null;
    }

    @Override
    public BedDetails getBedDetailsById(String id) {
        Bed bed = bedDao.getById(Integer.parseInt(id));
        if (bed != null) {
            List<BedPatientAssignment> currentAssignments = dao.getCurrentAssignmentsByBed(bed);
            Location location = dao.getWardForBed(bed);
            BedDetails bedDetails = constructBedDetails(bed, location, currentAssignments);
            return bedDetails;
        }
        return null;
    }

    @Override
    public BedDetails getBedDetailsByUuid(String uuid) {
        Bed bed = bedDao.getByUuid(uuid);
        if (bed != null) {
            List<BedPatientAssignment> currentAssignment = dao.getCurrentAssignmentsByBed(bed);
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

    @Override
    @Transactional
    public BedDetails getLatestBedDetailsByVisit(String visitUuid) {
        Bed bed = dao.getLatestBedByVisit(visitUuid);
        if (bed != null) {
            Location physicalLocation = dao.getWardForBed(bed);
            return constructBedDetails(bed, physicalLocation, new ArrayList<BedPatientAssignment>());
        }
        return null;
    }

    @Override
    public List<BedTag> getAllBedTags() {
        return dao.getAllBedTags();
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
