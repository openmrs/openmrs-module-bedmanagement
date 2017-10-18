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
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class BedManagementServiceImpl extends BaseOpenmrsService implements BedManagementService {

    BedManagementDAO dao;

    BedDAO bedDao;

    BedTypeDAO bedTypeDao;

    LocationService locationService;

    public void setDao(BedManagementDAO dao) {
        this.dao = dao;
    }

    public void setBedDao(BedDAO bedDao) {
        this.bedDao = bedDao;
    }

    public void setBedTypeDao(BedTypeDAO bedTypeDao) {
        this.bedTypeDao = bedTypeDao;
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
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
    public Bed saveBed(String uuid, SimpleObject properties) throws IllegalPropertyException {
        Bed bed;
        BedType bedType = null;
        if (properties.get("bedType") != null) {
            String bedTypeName = properties.get("bedType");
            bedType = bedTypeDao.getByName(bedTypeName);
            if (bedType == null)
                throw new IllegalPropertyException("Invalid bed type name");
        }

        if (uuid != null) {
            bed = getBedByUuid(uuid);
            if (bed == null)
                throw new IllegalPropertyException("Bed not exist");

            if (properties.get("bedNumber") != null)
                bed.setBedNumber((String) properties.get("bedNumber"));

            if (properties.get("status") != null)
                bed.setStatus((String) properties.get("status"));

            if (bedType != null) {
                bed.setBedType(bedType);
            }
        } else {
            bed = new Bed();
            if (properties.get("bedNumber") == null || properties.get("bedType") == null)
                throw new IllegalPropertyException("bedNumber & bedType should not be null");
            bed.setBedNumber((String) properties.get("bedNumber"));
            bed.setStatus(properties.get("status") != null ? (String) properties.get("status") : "AVAILABLE");
            bed.setBedType(bedType);
        }

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
    public BedType getBedTypeById(int id) {
        return bedTypeDao.getById(id);
    }

    @Override
    public List<BedType> listBedTypes(String name, Integer limit, Integer offset) {
        return bedTypeDao.getAll(name, limit, offset);
    }

    @Override
    public BedType saveBedType(BedType bedType) {
        bedTypeDao.save(bedType);
        return bedType;
    }

    @Override
    public BedType saveBedType(Integer id, SimpleObject properties) {
        BedType bedType;
        if (id != null) {
            bedType = getBedTypeById(id);
            if (bedType == null)
                throw new IllegalPropertyException("Bed Type not exist");

            if (properties.get("name") != null)
                bedType.setName((String) properties.get("name"));

            if (properties.get("displayName") != null)
                bedType.setDisplayName((String) properties.get("displayName"));

            if (properties.get("description") != null)
                bedType.setDescription((String) properties.get("description"));
        } else {
            bedType = new BedType();
            if (properties.get("name") == null || properties.get("displayName") == null)
                throw new IllegalPropertyException("Required parameters: name, displayName");
            bedType.setName((String) properties.get("name"));
            bedType.setDisplayName((String) properties.get("displayName"));
            bedType.setDescription((String) properties.get("description"));
        }

        bedTypeDao.save(bedType);
        return bedType;
    }

    @Override
    public void deleteBedType(BedType bedType) {
        bedTypeDao.delete(bedType);
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

    @Override
    public List<Location> getAllWards() {
        return dao.getWards();
    }

    @Override
    public List<Location> getWardsByName(String name) {
        return dao.searchWardByName(name);
    }

    @Override
    public Location getWardByUuid(String uuid) {
        return dao.getWardByUuid(uuid);
    }

    @Override
    public Long getTotalBeds(Location location) {
        return bedDao.getTotalBedByLocationUuid(location.getUuid());
    }

    @Override
    public Location saveWard(String uuid, SimpleObject properties) {
        Location ward;
        LocationTag admissionLocationTag = locationService.getLocationTagByName(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION);
        if (uuid != null) {
            ward = locationService.getLocationByUuid(uuid);
            if (ward == null || !ward.getTags().contains(admissionLocationTag))
                throw new IllegalPropertyException("Location not exist");

            if (properties.get("name") != null)
                ward.setName((String) properties.get("name"));

            if (properties.get("description") != null)
                ward.setDescription((String) properties.get("description"));
        } else {
            ward = new Location();
            if (properties.get("name") == null)
                throw new IllegalPropertyException("Required parameters: name");
            ward.setName((String) properties.get("name"));
            ward.setDescription((String) properties.get("description"));

            Set<LocationTag> locationTagSet = new HashSet<>();
            locationTagSet.add(admissionLocationTag);
            ward.setTags(locationTagSet);
        }

        locationService.saveLocation(ward);
        return ward;
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
