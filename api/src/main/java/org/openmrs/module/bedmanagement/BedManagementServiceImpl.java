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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class BedManagementServiceImpl extends BaseOpenmrsService implements BedManagementService {

    BedManagementDAO dao;

    BedDAO bedDao;

    BedTypeDAO bedTypeDao;

    BedLocationMappingDAO bedLocationMappingDao;

    @Autowired
    private LocationService locationService;

    public void setDao(BedManagementDAO dao) {
        this.dao = dao;
    }

    public void setBedDao(BedDAO bedDao) {
        this.bedDao = bedDao;
    }

    public void setBedTypeDao(BedTypeDAO bedTypeDao) {
        this.bedTypeDao = bedTypeDao;
    }

    public void setBedLocationMappingDao(BedLocationMappingDAO bedLocationMappingDao) {
        this.bedLocationMappingDao = bedLocationMappingDao;
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
    public List<Bed> listBeds(String locationUuid, String bedType, String status, Integer limit, Integer offset) {
        List<Bed> bedList = new ArrayList<>();
        if (bedType != null && status == null) {
            bedList = bedDao.searchByBedType(locationUuid, bedType, limit, offset);
        } else if (bedType == null && status != null) {
            bedList = bedDao.searchByBedStatus(locationUuid, status, limit, offset);
        } else if (bedType != null && status != null) {
            bedList = bedDao.searchByBedTypeAndStatus(locationUuid, bedType, status, limit, offset);
        } else {
            bedList = bedDao.getAll(locationUuid, limit, offset);
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
            if (properties.get("bedNumber") == null || properties.get("bedType") == null ||
                    properties.get("row") == null || properties.get("column") == null ||
                    properties.get("locationUuid") == null)
                throw new IllegalPropertyException("Required parameters: bedNumber, bedType, row, column, locationUuid");
            bed.setBedNumber((String) properties.get("bedNumber"));
            bed.setStatus(properties.get("status") != null ? (String) properties.get("status") : "AVAILABLE");
            bed.setBedType(bedType);
        }

        bedDao.save(bed);
        if (properties.get("row") != null && properties.get("column") != null &&
                properties.get("locationUuid") != null) {
            String locationUuid = properties.get("locationUuid");
            Integer row = properties.get("row");
            Integer column = properties.get("column");
            this.saveBedLocationMapping(locationUuid, row, column, bed);
        }

        return bed;
    }

    @Override
    public BedLocationMapping saveBedLocationMapping(String locationUuid, Integer row, Integer column, Bed bed){
        BedLocationMapping existingBedLocationMapping = bedDao.getBedLocationMappingByLocationAndLayout(locationUuid, row, column);
        if (existingBedLocationMapping != null && !existingBedLocationMapping.getBed().getId().equals(bed.getId()))
            throw new IllegalPropertyException("Already bed assign to give row & column");

        BedLocationMapping bedLocationMapping = bedDao.getBedLocationMappingByBedId(bed.getId());
        if (bedLocationMapping == null){
            bedLocationMapping = new BedLocationMapping();
        }

        Location location = locationService.getLocationByUuid(locationUuid);
        bedLocationMapping.setLocation(location);
        bedLocationMapping.setRow(row);
        bedLocationMapping.setColumn(column);
        bedLocationMapping.setBed(bed);
        return bedLocationMappingDao.save(bedLocationMapping);
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
    public BedLocationMapping getBedLocationMappingByBedId(Integer bedId) {
        return bedDao.getBedLocationMappingByBedId(bedId);
    }

    @Override
    public BedLocationMapping getBedLocationMappingByLocationAndLayout(String locationUuid, Integer row, Integer column) {
        return bedDao.getBedLocationMappingByLocationAndLayout(locationUuid, row, column);
    }

    @Override
    public Location saveWard(String uuid, SimpleObject properties) {
        Location ward = constructLocation(uuid, properties.get("name"), properties.get("description"));
        if (properties.get("room") != null) {
            HashMap<String, Object> roomProperties = properties.get("room");
            Location room = constructLocation(roomProperties.get("uuid"), roomProperties.get("name"), roomProperties.get("description"));
            room.setParentLocation(ward);
            Set<Location> rooms = ward.getChildLocations() != null ? ward.getChildLocations() : new HashSet<Location>();
            if (!rooms.contains(room)) {
                rooms.add(room);
                ward.setChildLocations(rooms);
            }
        }

        locationService.saveLocation(ward);
        return ward;
    }

    private Location constructLocation(Object uuid, Object name, Object description) {
        Location location = new Location();
        LocationTag admissionLocationTag = locationService.getLocationTagByName(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION);

        if (uuid != null) {
            location = locationService.getLocationByUuid((String) uuid);
            if (location == null || !location.getTags().contains(admissionLocationTag))
                throw new IllegalPropertyException("Location not exist");
        } else {
            Set<LocationTag> locationTagSet = new HashSet<>();
            locationTagSet.add(admissionLocationTag);
            location.setTags(locationTagSet);
        }

        if (uuid == null && name == null)
            throw new IllegalPropertyException("Missing required parameters: name");

        if (name != null)
            location.setName((String) name);

        if (description != null)
            location.setDescription((String) description);

        return location;
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
