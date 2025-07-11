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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.APIException;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Transactional
public class BedManagementServiceImpl extends BaseOpenmrsService implements BedManagementService {
	
	private BedManagementDao bedManagementDao;
	
	private LocationService locationService;
	
	private final Log log = LogFactory.getLog(getClass());
	
	public void setDao(BedManagementDao dao) {
		this.bedManagementDao = dao;
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<AdmissionLocation> getAdmissionLocations() {
		LocationTag admissionLocationTag = locationService
		        .getLocationTagByName(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION);
		List<Location> locations = locationService.getLocationsByTag(admissionLocationTag);
		return bedManagementDao.getAdmissionLocations(locations);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<BedLocationMapping> getBedLocationMappingsByLocation(Location location) {
		return bedManagementDao.getBedLocationMappingsByLocation(location);
	}
	
	@Override
	@Transactional(readOnly = true)
	public AdmissionLocation getAdmissionLocationByLocation(Location location) {
		return bedManagementDao.getAdmissionLocationForLocation(location);
	}
	
	@Override
	@Transactional
	public AdmissionLocation saveAdmissionLocation(AdmissionLocation admissionLocation) {
		Location location = admissionLocation.getWard();
		locationService.saveLocation(location);
		admissionLocation = bedManagementDao.getAdmissionLocationForLocation(location);
		return admissionLocation;
	}
	
	@Override
	@Transactional
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
					throw new APIException("Cannot downsize bed layout with existing bed in the way at row "
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
		BedDetails prev = getBedManagementService().unAssignPatientFromBed(patient);
		Bed bed = bedManagementDao.getBedById(Integer.parseInt(bedId));
		
		BedPatientAssignment bedPatientAssignment = new BedPatientAssignment();
		bedPatientAssignment.setPatient(patient);
		bedPatientAssignment.setEncounter(encounter);
		bedPatientAssignment.setBed(bed);
		bedPatientAssignment.setStartDatetime(encounter.getEncounterDatetime());
		bedManagementDao.saveBedPatientAssignment(bedPatientAssignment);
		
		bed.setStatus(BedStatus.OCCUPIED.toString());
		bed = bedManagementDao.saveBed(bed);
		
		BedDetails current = getBedDetails(bed);
		BedPatientAssignment prevAssignment = (prev != null) ? prev.getLastAssignment() : null;
		current.setLastAssignment(prevAssignment);
		return current;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Bed getBedById(int id) {
		return bedManagementDao.getBedById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BedDetails getBedAssignmentDetailsByPatient(Patient patient) {
		Bed bed = bedManagementDao.getBedByPatient(patient);
		return getBedDetails(bed);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BedDetails getBedDetailsById(String id) {
		Bed bed = bedManagementDao.getBedById(Integer.parseInt(id));
		return getBedDetails(bed);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BedDetails getBedDetailsByUuid(String uuid) {
		Bed bed = bedManagementDao.getBedByUuid(uuid);
		return getBedDetails(bed);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BedPatientAssignment getBedPatientAssignmentByUuid(String uuid) {
		return bedManagementDao.getBedPatientAssignmentByUuid(uuid);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<BedPatientAssignment> getBedPatientAssignmentByEncounter(String encounterUuid, boolean includeEnded) {
		return bedManagementDao.getBedPatientAssignmentByEncounter(encounterUuid, includeEnded);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<BedPatientAssignment> getBedPatientAssignmentByVisit(String visitUuid, boolean includeEnded) {
		return bedManagementDao.getBedPatientAssignmentByVisit(visitUuid, includeEnded);
	}
	
	@Override
	@Transactional
	public BedDetails unAssignPatientFromBed(Patient patient) {
		Bed bed = bedManagementDao.getBedByPatient(patient);
		return unassignBed(bed, patient, new Date());
	}
	
	private BedDetails unassignBed(Bed bed, Patient patient, Date bedAssignmentEndDatetime) {
		BedPatientAssignment endedAssignment = null;
		if (bed != null) {
			List<BedPatientAssignment> currentAssignments = bedManagementDao.getCurrentAssignmentsByBed(bed);
			BedStatus finalStatus = BedStatus.AVAILABLE;
			for (BedPatientAssignment assignment : currentAssignments) {
				if (assignment.getPatient().equals(patient)) {
					assignment.setEndDatetime(bedAssignmentEndDatetime);
					endedAssignment = bedManagementDao.saveBedPatientAssignment(assignment);
				} else {
					finalStatus = BedStatus.OCCUPIED;
				}
			}
			bed.setStatus(finalStatus.toString());
			bedManagementDao.saveBed(bed);
		}
		BedDetails bedDetails = getBedDetails(bed);
		if (bedDetails != null) {
			bedDetails.setLastAssignment(endedAssignment);
		}
		return bedDetails;
	}
	
	@Override
	@Transactional
	public List<BedDetails> unAssignBedsInEndedVisit(Visit visit) {
		if (visit == null || visit.getId() == null || visit.getStopDatetime() == null) {
			throw new APIException("Visit does not exist or has not ended");
		}
		Patient patient = visit.getPatient();
		List<BedPatientAssignment> bedassignments = bedManagementDao.getBedPatientAssignmentByVisit(visit.getUuid(), false);
		List<BedDetails> unassignedBeds = new ArrayList<>();
		for (BedPatientAssignment bpa : bedassignments) {
			BedDetails unassignedBed = unassignBed(bpa.getBed(), patient, visit.getStopDatetime());
			log.debug("Unassigned bed " + unassignedBed);
			unassignedBeds.add(unassignedBed);
		}
		return unassignedBeds;
	}
	
	@Override
	@Transactional
	public BedPatientAssignment saveBedPatientAssignment(BedPatientAssignment bpa) {
		return bedManagementDao.saveBedPatientAssignment(bpa);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BedDetails getLatestBedDetailsByVisit(String visitUuid) {
		Bed bed = bedManagementDao.getLatestBedByVisit(visitUuid);
		return getBedDetails(bed);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<BedTag> getAllBedTags() {
		return bedManagementDao.getAllBedTags();
	}
	
	@Override
	@Transactional(readOnly = true)
	public BedLocationMapping getBedLocationMappingByBedId(Integer bedId) {
		Bed bed = bedManagementDao.getBedById(bedId);
		return bedManagementDao.getBedLocationMappingByBed(bed);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Bed> getBeds(Integer limit, Integer offset) {
		return bedManagementDao.getBeds(null, null, null, limit, offset);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Bed> getBeds(String locationUuid, String bedTypeName, BedStatus status, Integer limit, Integer offset) {
		Location location = locationUuid != null ? locationService.getLocationByUuid(locationUuid) : null;
		BedType bedType = null;
		if (bedTypeName != null) {
			List<BedType> bedTypes = bedManagementDao.getBedTypes(bedTypeName, 1, 0);
			if (bedTypes.size() == 0)
				throw new APIException("Invalid bed type name");
			bedType = bedTypes.get(0);
		}
		
		return bedManagementDao.getBeds(location, bedType, status, limit, offset);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Bed getBedByUuid(String uuid) {
		return bedManagementDao.getBedByUuid(uuid);
	}
	
	@Override
	@Transactional
	public void deleteBed(Bed bed, String reason) throws BedOccupiedException {
		if (BedStatus.OCCUPIED.toString().equals(bed.getStatus())) {
			throw new BedOccupiedException(bed);
		}
		BedLocationMapping bedLocationMapping = bedManagementDao.getBedLocationMappingByBed(bed);
		if (bedLocationMapping != null) {
			bedManagementDao.deleteBedLocationMapping(bedLocationMapping);
		}
		
		bed.setVoided(true);
		bed.setDateVoided(new Date());
		bed.setVoidReason(reason);
		bed.setVoidedBy(Context.getAuthenticatedUser());
		bedManagementDao.saveBed(bed);
	}
	
	@Override
	@Transactional
	public Bed saveBed(Bed bed) {
		return bedManagementDao.saveBed(bed);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BedTag getBedTagByUuid(String uuid) {
		return bedManagementDao.getBedTagByUuid(uuid);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<BedTag> getBedTags(String name, Integer limit, Integer offset) {
		return bedManagementDao.getBedTags(name, limit, offset);
	}
	
	@Override
	@Transactional
	public BedTag saveBedTag(BedTag bedTag) {
		return bedManagementDao.saveBedTag(bedTag);
	}
	
	@Override
	@Transactional
	public void deleteBedTag(BedTag bedTag, String reason) {
		bedTag.setVoided(true);
		bedTag.setVoidReason(reason);
		bedTag.setVoidedBy(Context.getAuthenticatedUser());
		bedManagementDao.saveBedTag(bedTag);
	}
	
	@Override
	@Transactional
	public BedLocationMapping saveBedLocationMapping(BedLocationMapping bedLocationMapping) {
		Location location = bedLocationMapping.getLocation();
		BedLocationMapping existingBedLocationMapping = bedManagementDao.getBedLocationMappingByLocationAndRowAndColumn(
		    location, bedLocationMapping.getRow(), bedLocationMapping.getColumn());
		
		if (existingBedLocationMapping != null && existingBedLocationMapping.getBed() == null) {
			existingBedLocationMapping.setBed(bedLocationMapping.getBed());
			bedLocationMapping = existingBedLocationMapping;
		} else if (existingBedLocationMapping != null && existingBedLocationMapping.getBed() != null
		        && !existingBedLocationMapping.getBed().getId().equals(bedLocationMapping.getBed().getId())) {
			throw new APIException("Already bed assign to give row & column");
		}
		
		return bedManagementDao.saveBedLocationMapping(bedLocationMapping);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<BedType> getBedTypes(String name, Integer limit, Integer offset) {
		return bedManagementDao.getBedTypes(name, limit, offset);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BedLocationMapping getBedLocationMappingByLocationUuidAndRowColumn(String locationUuid, Integer row,
	        Integer column) {
		Location location = locationService.getLocationByUuid(locationUuid);
		return bedManagementDao.getBedLocationMappingByLocationAndRowAndColumn(location, row, column);
	}
	
	@Override
	@Transactional(readOnly = true)
	public BedType getBedTypeByUuid(String uuid) {
		return bedManagementDao.getBedTypeByUuid(uuid);
	}
	
	@Override
	@Transactional
	public BedType saveBedType(BedType bedType) {
		if (bedType.getRetired()) {
			return getBedManagementService().retireBedType(bedType, bedType.getRetireReason());
		}
		return bedManagementDao.saveBedType(bedType);
	}
	
	@Override
	@Transactional
	public void deleteBedType(BedType bedType) {
		bedManagementDao.deleteBedType(bedType);
	}
	
	@Override
	@Transactional
	public BedType retireBedType(BedType bedType, String retireReason) {
		if (StringUtils.isBlank(retireReason)) {
			throw new APIException("BedType.retiring.reason.required", (Object[]) null);
		}
		bedType.setRetired(true);
		bedType.setRetireReason(retireReason);
		bedType.setRetiredBy(Context.getAuthenticatedUser());
		bedType.setDateRetired(new Date());
		return bedManagementDao.saveBedType(bedType);
	}
	
	@Override
	@Transactional
	public void deleteBedPatientAssignment(BedPatientAssignment bpa, String reason) {
		
		bpa.setVoided(true);
		bpa.setDateVoided(new Date());
		bpa.setVoidReason(reason);
		bpa.setVoidedBy(Context.getAuthenticatedUser());
		bedManagementDao.saveBedPatientAssignment(bpa);
	}
	
	private BedDetails getBedDetails(Bed bed) {
		if (bed == null) {
			return null;
		}
		List<BedPatientAssignment> currentAssignments = bedManagementDao.getCurrentAssignmentsByBed(bed);
		Location location = bedManagementDao.getWardForBed(bed);
		BedDetails bedDetails = new BedDetails();
		bedDetails.setBed(bed);
		bedDetails.setBedNumber(bed.getBedNumber());
		List<Patient> patients = new ArrayList<>();
		for (BedPatientAssignment assignment : currentAssignments) {
			patients.add(assignment.getPatient());
		}
		bedDetails.setPatients(patients);
		bedDetails.setCurrentAssignments(currentAssignments);
		bedDetails.setPhysicalLocation(location);
		bedDetails.setBedType(bed.getBedType());
		return bedDetails;
	}
	
	protected BedManagementService getBedManagementService() {
		return Context.getService(BedManagementService.class);
	}
}
