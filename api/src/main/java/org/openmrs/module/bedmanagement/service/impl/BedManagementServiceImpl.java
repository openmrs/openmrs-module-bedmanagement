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
package org.openmrs.module.bedmanagement.service.impl;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.dao.*;
import org.openmrs.module.bedmanagement.entity.*;
import org.openmrs.module.bedmanagement.pojo.AdmissionLocation;
import org.openmrs.module.bedmanagement.pojo.BedDetails;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BedManagementServiceImpl extends BaseOpenmrsService implements BedManagementService {

    BedDao bedDao;

    BedTypeDao bedTypeDao;

    BedLocationMappingDao bedLocationMappingDao;

    AdmissionLocationDao admissionLocationDao;

    BedPatientAssignmentDao bedPatientAssignmentDao;

    @Autowired
    private LocationService locationService;

    public void setBedDao(BedDao bedDao) {
        this.bedDao = bedDao;
    }

    public void setBedTypeDao(BedTypeDao bedTypeDao) {
        this.bedTypeDao = bedTypeDao;
    }

    public void setAdmissionLocationDao(AdmissionLocationDao admissionLocationDao) {
        this.admissionLocationDao = admissionLocationDao;
    }

    public void setBedPatientAssignmentDao(BedPatientAssignmentDao bedPatientAssignmentDao) {
        this.bedPatientAssignmentDao = bedPatientAssignmentDao;
    }

    public void setBedLocationMappingDao(BedLocationMappingDao bedLocationMappingDao) {
        this.bedLocationMappingDao = bedLocationMappingDao;
    }

    @Override
    public List<AdmissionLocation> getAdmissionLocations() {
        return admissionLocationDao.getAdmissionLocations();
    }

    @Override
    public AdmissionLocation getAdmissionLocationByLocation(Location location) {
        return admissionLocationDao.getAdmissionLocationsByLocation(location);
    }

    @Override
    public AdmissionLocation saveAdmissionLocation(AdmissionLocation admissionLocation) {
        Location location = admissionLocation.getWard();
        locationService.saveLocation(location);
        admissionLocation = admissionLocationDao.getAdmissionLocationsByLocation(location);
        return admissionLocation;
    }

    @Override
    public AdmissionLocation setBedLayoutForAdmissionLocation(AdmissionLocation admissionLocation, Integer row, Integer column) {
        Location location = admissionLocation.getWard();
        for (int i = 1; i <= row; i++) {
            for (int j = 1; j <= column; j++) {
                BedLocationMapping bedLocationMapping = bedLocationMappingDao.getBedLocationMappingByLocationAndRowAndColumn(location, i, j);
                if (bedLocationMapping == null) {
                    bedLocationMapping = new BedLocationMapping();
                    bedLocationMapping.setLocation(location);
                    bedLocationMapping.setRow(i);
                    bedLocationMapping.setColumn(j);
                    bedLocationMappingDao.saveBedLocationMapping(bedLocationMapping);
                }
            }
        }

        admissionLocation = admissionLocationDao.getAdmissionLocationsByLocation(location);
        return admissionLocation;
    }

    @Override
    public List<BedLocationMapping> getBedLocationMappingByLocation(Location location) {
        return bedLocationMappingDao.getBedLocationMappingByLocation(location);
    }

    @Override
    @Transactional
    public BedDetails assignPatientToBed(Patient patient, Encounter encounter, String bedId) {
        BedDetails prev = this.unassignPatientFromBed(patient);
        Bed bed = bedDao.getBedById(Integer.parseInt(bedId));
        BedDetails current = bedDao.assignPatientToBed(patient, encounter, bed);
        BedPatientAssignment prevAssignment = (prev != null) ? prev.getLastAssignment() : null;
        current.setLastAssignment(prevAssignment);
        return current;
    }

    @Override
    public Bed getBedById(Integer id) {
        return bedDao.getBedById(id);
    }

    @Override
    public Bed getBedByUuid(String uuid) {
        return bedDao.getBedByUuid(uuid);
    }

    @Override
    public List<Bed> getBeds(String locationUuid, String bedTypeName, BedStatus status, Integer limit, Integer offset) {
        List<Bed> bedList;
        Location location = locationUuid != null ? locationService.getLocationByUuid(locationUuid) : null;
        BedType bedType = null;
        if (bedTypeName != null) {
            List<BedType> bedTypes = bedTypeDao.getBedTypesByName(bedTypeName, 1, 0);
            if (bedTypes.size() == 0)
                throw new IllegalPropertyException("Invalid bed type name");
            bedType = bedTypes.get(0);
        }

        if (bedTypeName != null && status == null) {
            bedList = locationUuid != null ? bedDao.getBedsByLocationAndBedType(location, bedType, limit, offset) :
                    bedDao.getBedsByBedType(bedType, limit, offset);
        } else if (bedTypeName == null && status != null) {
            bedList = locationUuid != null ? bedDao.getBedsByLocationAndStatus(location, status, limit, offset) :
                    bedDao.getBedsByStatus(status, limit, offset);
        } else if (bedTypeName != null && status != null) {
            bedList = locationUuid != null ? bedDao.getBedsByLocationAndBedTypeAndStatus(location, bedType, status, limit, offset) :
                    bedDao.getBedsByBedTypeAndStatus(bedType, status, limit, offset);
        } else {
            bedList = locationUuid != null ? bedDao.getBedsByLocation(location, limit, offset) : bedDao.getBeds(limit, offset);
        }

        return bedList;
    }

    @Override
    public Bed saveBed(Bed bed) {
        bedDao.saveBed(bed);
        return bed;
    }

    @Override
    public BedLocationMapping saveBedLocationMapping(BedLocationMapping bedLocationMapping) {
        Location location = bedLocationMapping.getLocation();
        BedLocationMapping existingBedLocationMapping = bedLocationMappingDao
                .getBedLocationMappingByLocationAndRowAndColumn(location, bedLocationMapping.getRow(), bedLocationMapping.getColumn());

        if (existingBedLocationMapping != null && existingBedLocationMapping.getBed() == null) {
            existingBedLocationMapping.setBed(bedLocationMapping.getBed());
            bedLocationMapping = existingBedLocationMapping;
        } else if (existingBedLocationMapping != null && existingBedLocationMapping.getBed() != null
                && !existingBedLocationMapping.getBed().getId().equals(bedLocationMapping.getBed().getId())) {
            throw new IllegalPropertyException("Already bed assign to give row & column");
        }

        return bedLocationMappingDao.saveBedLocationMapping(bedLocationMapping);
    }

    @Override

    public void deleteBed(Bed bed, String reason) {
        BedLocationMapping bedLocationMapping =  bedLocationMappingDao.getBedLocationMappingByBed(bed);
        if(bedLocationMapping != null){
            bedLocationMapping.setBed(null);
            bedLocationMappingDao.saveBedLocationMapping(bedLocationMapping);
        }

        bed.setVoided(true);
        bed.setDateVoided(new Date());
        bed.setVoidReason(reason);
        bed.setVoidedBy(Context.getAuthenticatedUser());
        bedDao.saveBed(bed);
    }

    @Override
    public BedType getBedTypeById(Integer id) {
        return bedTypeDao.getBedTypeById(id);
    }

    @Override
    public List<BedType> getBedTypesByName(String name, Integer limit, Integer offset) {
        return name != null ? bedTypeDao.getBedTypesByName(name, limit, offset) : bedTypeDao.getBedTypes(limit, offset);
    }

    @Override
    public BedType saveBedType(BedType bedType) {
        bedTypeDao.saveBedType(bedType);
        return bedType;
    }

    @Override
    public void deleteBedType(BedType bedType) {
        bedTypeDao.deleteBedType(bedType);
    }

    @Override
    public BedDetails getBedAssignmentDetailsByPatient(Patient patient) {
        Bed bed = bedDao.getBedByPatient(patient);
        if (bed != null) {
            List<BedPatientAssignment> currentAssignments = bedPatientAssignmentDao.getCurrentAssignmentsForBed(bed);
            Location physicalLocation = admissionLocationDao.getWardForBed(bed);
            return constructBedDetails(bed, physicalLocation, currentAssignments);
        }
        return null;
    }

    @Override
    public BedDetails getBedDetailsByBedId(String bedId) {
        Bed bed = bedDao.getBedById(Integer.parseInt(bedId));
        if (bed != null) {
            List<BedPatientAssignment> currentAssignments = bedPatientAssignmentDao.getCurrentAssignmentsForBed(bed);
            Location location = admissionLocationDao.getWardForBed(bed);
            BedDetails bedDetails = constructBedDetails(bed, location, currentAssignments);
            return bedDetails;
        }
        return null;
    }

    @Override
    public BedDetails getBedDetailsByBedUuid(String bedUuid) {
        Bed bed = bedDao.getBedByUuid(bedUuid);
        if (bed != null) {
            List<BedPatientAssignment> currentAssignment = bedPatientAssignmentDao.getCurrentAssignmentsForBed(bed);
            Location location = admissionLocationDao.getWardForBed(bed);
            BedDetails bedDetails = constructBedDetails(bed, location, currentAssignment);
            return bedDetails;
        }
        return null;
    }

    @Override
    public BedPatientAssignment getBedPatientAssignmentByUuid(String bedUuid) {
        return bedPatientAssignmentDao.getBedPatientAssignmentByUuid(bedUuid);
    }

    @Override
    @Transactional
    public BedDetails unassignPatientFromBed(Patient patient) {
        Bed currentBed = bedDao.getBedByPatient(patient);
        if (currentBed != null) {
            return bedDao.unassignPatient(patient, currentBed);
        }
        return null;
    }

    @Override
    @Transactional
    public BedDetails getLatestBedDetailsByVisitUuid(String visitUuid) {
        Bed bed = bedDao.getLatestBedByVisitUuid(visitUuid);
        if (bed != null) {
            Location physicalLocation = admissionLocationDao.getWardForBed(bed);
            return constructBedDetails(bed, physicalLocation, new ArrayList<BedPatientAssignment>());
        }
        return null;
    }

    @Override
    public List<BedTag> getAllBedTags() {
        return bedDao.getAllBedTags();
    }

    @Override
    public Long getBedCount(Location location) {
        return bedDao.getBedCountByLocation(location);
    }

    @Override
    public BedLocationMapping getBedLocationMappingByBedId(Integer bedId) {
        Bed bed = bedDao.getBedById(bedId);
        return bedLocationMappingDao.getBedLocationMappingByBed(bed);
    }

    @Override
    public BedLocationMapping getBedLocationMappingByLocationUuidAndRowColumn(String locationUuid, Integer row, Integer column) {
        Location location = locationService.getLocationByUuid(locationUuid);
        return bedLocationMappingDao.getBedLocationMappingByLocationAndRowAndColumn(location, row, column);
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
