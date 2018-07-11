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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.bedmanagement.AdmissionLocation;
import org.openmrs.module.bedmanagement.BedDetails;
import org.openmrs.module.bedmanagement.constants.BedManagementApiConstants;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.dao.BedManagementDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.module.bedmanagement.exception.BedOccupiedException;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class BedManagementServiceImpl extends BaseOpenmrsService implements BedManagementService {
	
	private BedManagementDao bedManagementDao;
	
	private LocationService locationService;
	
	public void setDao(BedManagementDao dao) {
		this.bedManagementDao = dao;
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	@Override
	public List<AdmissionLocation> getAdmissionLocations() {
		LocationTag admissionLocationTag = locationService
		        .getLocationTagByName(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION);
		List<Location> locations = locationService.getLocationsByTag(admissionLocationTag);
		return bedManagementDao.getAdmissionLocations(locations);
	}
	
	@Override
	public List<BedLocationMapping> getBedLocationMappingsByLocation(Location location) {
		return bedManagementDao.getBedLocationMappingsByLocation(location);
	}
	
	@Override
	public AdmissionLocation getAdmissionLocationByLocation(Location location) {
		return bedManagementDao.getAdmissionLocationForLocation(location);
	}
	
	@Override
	public AdmissionLocation saveAdmissionLocation(AdmissionLocation admissionLocation) {
		Location location = admissionLocation.getWard();
		locationService.saveLocation(location);
		admissionLocation = bedManagementDao.getAdmissionLocationForLocation(location);
		return admissionLocation;
	}
	
	@Override
	public AdmissionLocation setBedLayoutForAdmissionLocation(AdmissionLocation admissionLocation, Integer row,
	        Integer column) {
		Location location = admissionLocation.getWard();
		for (int i = 1; i <= row; i++) {
			for (int j = 1; j <= column; j++) {
				BedLocationMapping bedLocationMapping = bedManagementDao
				        .getBedLocationMappingByLocationAndRowAndColumn(location, i, j);
				if (bedLocationMapping == null) {
					bedLocationMapping = new BedLocationMapping();
					bedLocationMapping.setLocation(location);
					bedLocationMapping.setRow(i);
					bedLocationMapping.setColumn(j);
					bedManagementDao.saveBedLocationMapping(bedLocationMapping);
				}
			}
		}
		
		List<BedLocationMapping> bedLocationMappings = bedManagementDao
		        .getBedLocationMappingsByLocation(admissionLocation.getWard());
		for (BedLocationMapping bedlocationMapping : bedLocationMappings) {
			if (bedlocationMapping.getRow() > row || bedlocationMapping.getColumn() > column) {
				if (bedlocationMapping.getBed() == null) {
					bedManagementDao.deleteBedLocationMapping(bedlocationMapping);
				} else {
					throw new IllegalPropertyException("Cannot downsize bed layout with existing bed in the way at row "
					        + bedlocationMapping.getRow() + " and column " + bedlocationMapping.getColumn());
				}
			}
		}
		
		admissionLocation = bedManagementDao.getAdmissionLocationForLocation(location);
		return admissionLocation;
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
		return bedManagementDao.getAllBedTags();
	}
	
	@Override
	public BedLocationMapping getBedLocationMappingByBedId(Integer bedId) {
		Bed bed = bedManagementDao.getBedById(bedId);
		return bedManagementDao.getBedLocationMappingByBed(bed);
	}
	
	@Override
	public List<Bed> getBeds(Integer limit, Integer offset) {
		return bedManagementDao.getBeds(null, null, null, limit, offset);
	}
	
	@Override
	public List<Bed> getBeds(String locationUuid, String bedTypeName, BedStatus status, Integer limit, Integer offset) {
		Location location = locationUuid != null ? locationService.getLocationByUuid(locationUuid) : null;
		BedType bedType = null;
		if (bedTypeName != null) {
			List<BedType> bedTypes = bedManagementDao.getBedTypes(bedTypeName, 1, 0);
			if (bedTypes.size() == 0)
				throw new IllegalPropertyException("Invalid bed type name");
			bedType = bedTypes.get(0);
		}
		
		return bedManagementDao.getBeds(location, bedType, status, limit, offset);
	}
	
	@Override
	public Bed getBedByUuid(String uuid) {
		return bedManagementDao.getBedByUuid(uuid);
	}
	
	@Override
	public void deleteBed(Bed bed, String reason) throws BedOccupiedException {
		if (BedStatus.OCCUPIED.toString().equals(bed.getStatus())) {
			throw new BedOccupiedException(bed);
		}
		BedLocationMapping bedLocationMapping = bedManagementDao.getBedLocationMappingByBed(bed);
		if (bedLocationMapping != null) {
			bedLocationMapping.setBed(null);
			bedManagementDao.saveBedLocationMapping(bedLocationMapping);
		}
		
		bed.setVoided(true);
		bed.setDateVoided(new Date());
		bed.setVoidReason(reason);
		bed.setVoidedBy(Context.getAuthenticatedUser());
		bedManagementDao.saveBed(bed);
	}
	
	@Override
	public Bed saveBed(Bed bed) {
		return bedManagementDao.saveBed(bed);
	}
	
	@Override
	public BedTag getBedTagByUuid(String uuid) {
		return bedManagementDao.getBedTagByUuid(uuid);
	}
	
	@Override
	public List<BedTag> getBedTags(String name, Integer limit, Integer offset) {
		return bedManagementDao.getBedTags(name, limit, offset);
	}
	
	@Override
	public BedTag saveBedTag(BedTag bedTag) {
		return bedManagementDao.saveBedTag(bedTag);
	}
	
	@Override
	public void deleteBedTag(BedTag bedTag, String reason) {
		bedTag.setVoided(true);
		bedTag.setVoidReason(reason);
		bedTag.setVoidedBy(Context.getAuthenticatedUser());
		bedManagementDao.saveBedTag(bedTag);
	}
	
	@Override
	public BedLocationMapping saveBedLocationMapping(BedLocationMapping bedLocationMapping) {
		Location location = bedLocationMapping.getLocation();
		BedLocationMapping existingBedLocationMapping = bedManagementDao.getBedLocationMappingByLocationAndRowAndColumn(
		    location, bedLocationMapping.getRow(), bedLocationMapping.getColumn());
		
		if (existingBedLocationMapping != null && existingBedLocationMapping.getBed() == null) {
			existingBedLocationMapping.setBed(bedLocationMapping.getBed());
			bedLocationMapping = existingBedLocationMapping;
		} else if (existingBedLocationMapping != null && existingBedLocationMapping.getBed() != null
		        && !existingBedLocationMapping.getBed().getId().equals(bedLocationMapping.getBed().getId())) {
			throw new IllegalPropertyException("Already bed assign to give row & column");
		}
		
		return bedManagementDao.saveBedLocationMapping(bedLocationMapping);
	}
	
	@Override
	public List<BedType> getBedTypes(String name, Integer limit, Integer offset) {
		return bedManagementDao.getBedTypes(name, limit, offset);
	}
	
	@Override
	public BedLocationMapping getBedLocationMappingByLocationUuidAndRowColumn(String locationUuid, Integer row,
	        Integer column) {
		Location location = locationService.getLocationByUuid(locationUuid);
		return bedManagementDao.getBedLocationMappingByLocationAndRowAndColumn(location, row, column);
	}
	
	@Override
	public BedType getBedTypeByUuid(String uuid) {
		return bedManagementDao.getBedTypeByUuid(uuid);
	}
	
	@Override
	public BedType saveBedType(BedType bedType) {
		return bedManagementDao.saveBedType(bedType);
	}
	
	@Override
	public void deleteBedType(BedType bedType) {
		bedManagementDao.deleteBedType(bedType);
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
