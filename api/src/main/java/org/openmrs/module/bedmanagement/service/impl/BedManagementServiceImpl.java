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
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.bedmanagement.constants.BedManagementApiConstants;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.dao.*;
import org.openmrs.module.bedmanagement.entity.*;
import org.openmrs.module.bedmanagement.pojo.AdmissionLocation;
import org.openmrs.module.bedmanagement.pojo.BedDetails;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    public List<AdmissionLocation> getAllAdmissionLocations() {
        return admissionLocationDao.getAdmissionLocationsByLocationTagName(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION);
    }

    @Override
    public AdmissionLocation getLayoutForWard(Location location) {
        return admissionLocationDao.getLayoutForWard(location);
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
    public Bed saveBed(String uuid, SimpleObject properties) throws IllegalPropertyException {
        Bed bed;
        BedType bedType = null;
        if (properties.get("bedType") != null) {
            String bedTypeName = properties.get("bedType");
            List<BedType> bedTypes = bedTypeDao.getBedTypesByName(bedTypeName, 1, 0);
            if (bedTypes.size() == 0)
                throw new IllegalPropertyException("Invalid bed type name");
            bedType = bedTypes.get(0);
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

        bedDao.saveBed(bed);
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
    public BedLocationMapping saveBedLocationMapping(String locationUuid, Integer row, Integer column, Bed bed) {
        Location location = locationService.getLocationByUuid(locationUuid);
        BedLocationMapping existingBedLocationMapping = bedLocationMappingDao.getBedLocationMappingByLocationAndRowAndColumn(location, row, column);
        if (existingBedLocationMapping != null && existingBedLocationMapping.getBed() != null && !existingBedLocationMapping.getBed().getId().equals(bed.getId()))
            throw new IllegalPropertyException("Already bed assign to give row & column");

        BedLocationMapping bedLocationMapping = bedLocationMappingDao.getBedLocationMappingByBed(bed);
        if (bedLocationMapping == null) {
            bedLocationMapping = new BedLocationMapping();
        }

        bedLocationMapping.setLocation(location);
        bedLocationMapping.setRow(row);
        bedLocationMapping.setColumn(column);
        bedLocationMapping.setBed(bed);
        return bedLocationMappingDao.saveBedLocationMapping(bedLocationMapping);
    }

    @Override
    public void deleteBed(Bed bed, String reason) {
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
    public List<BedType> getBedTypes(String name, Integer limit, Integer offset) {
        return name != null ? bedTypeDao.getBedTypesByName(name, limit, offset) : bedTypeDao.getBedTypes(limit, offset);
    }

    @Override
    public BedType saveBedType(BedType bedType) {
        bedTypeDao.saveBedType(bedType);
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
    public List<Location> getAllWards() {
        return admissionLocationDao.getWards();
    }

    @Override
    public List<Location> getWardsByName(String name) {
        return admissionLocationDao.getWardsByName(name);
    }

    @Override
    public Location getWardByUuid(String uuid) {
        return admissionLocationDao.getWardByLocationUuid(uuid);
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
