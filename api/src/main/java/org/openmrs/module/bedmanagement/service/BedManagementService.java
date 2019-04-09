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
package org.openmrs.module.bedmanagement.service;

import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.bedmanagement.AdmissionLocation;
import org.openmrs.module.bedmanagement.BedDetails;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.module.bedmanagement.exception.BedOccupiedException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface BedManagementService extends OpenmrsService {
	
	/**
	 * Get all admission locations
	 *
	 * @return {@link List<AdmissionLocation>}
	 */
	@Authorized({ "Get Admission Locations", "Get Beds" })
	List<AdmissionLocation> getAdmissionLocations();
	
	/**
	 * Get bed location mapping by location
	 *
	 * @param location {@link Location}
	 * @return {@link List< BedLocationMapping >}
	 */
	@Authorized({ "Get Admission Locations", "Get Beds" })
	List<BedLocationMapping> getBedLocationMappingsByLocation(Location location);
	
	/**
	 * Get admission location by location
	 *
	 * @param location {@link Location}
	 * @return
	 */
	@Authorized({ "Get Admission Locations", "Get Beds" })
	AdmissionLocation getAdmissionLocationByLocation(Location location);
	
	/**
	 * Save / Update admission location
	 *
	 * @param admissionLocation
	 * @return
	 */
	@Authorized({ "Edit Admission Locations", "Manage Locations" })
	AdmissionLocation saveAdmissionLocation(AdmissionLocation admissionLocation);
	
	/**
	 * Set bed location mapping for admission location
	 *
	 * @param admissionLocation {@link AdmissionLocation}
	 * @param row {@link Integer} admission location bed layout row
	 * @param column {@link Integer} admission location bed layout column
	 * @return {@link AdmissionLocation}
	 */
	@Authorized({ "Edit Admission Locations", "Manage Locations" })
	AdmissionLocation setBedLayoutForAdmissionLocation(AdmissionLocation admissionLocation, Integer row, Integer column);
	
	@Authorized(value = { "Assign Beds", "Edit Admission Locations" }, requireAll = true)
	BedDetails assignPatientToBed(Patient patient, Encounter encounter, String bedId);
	
	Bed getBedById(int id);
	
	@Authorized(value = { "Get Beds", "Get Admission Locations" }, requireAll = true)
	BedDetails getBedAssignmentDetailsByPatient(Patient patient);
	
	@Authorized(value = { "Get Admission Locations", "Get Beds" }, requireAll = true)
	BedDetails getBedDetailsById(String id);
	
	@Authorized(value = { "Get Admission Locations", "Get Beds" }, requireAll = true)
	BedDetails getBedDetailsByUuid(String uuid);
	
	@Authorized(value = { "Get Admission Locations", "Get Beds" }, requireAll = true)
	BedPatientAssignment getBedPatientAssignmentByUuid(String uuid);
	
	@Authorized(value = { "Assign Beds", "Edit Admission Locations" }, requireAll = true)
	BedDetails unAssignPatientFromBed(Patient patient);
	
	@Authorized(value = { "Get Beds", "Get Admission Locations" }, requireAll = true)
	BedDetails getLatestBedDetailsByVisit(String visitUuid);
	
	@Authorized(value = { "Get Bed Tags" }, requireAll = true)
	List<BedTag> getAllBedTags();
	
	/**
	 * Get bed location mapping by bed id
	 *
	 * @param bedId {@link Integer} bed Id
	 * @return {@link BedLocationMapping}
	 */
	@Authorized(value = "Get Beds", requireAll = true)
	BedLocationMapping getBedLocationMappingByBedId(Integer bedId);
	
	/**
	 * Get all beds
	 *
	 * @param limit {@link Integer} limit result set, return all result set if limit is null
	 * @param offset {@link Integer} specify the starting row offset into the result set
	 * @return {@link List<Bed>}
	 */
	@Authorized(value = { "Get Beds" }, requireAll = true)
	List<Bed> getBeds(Integer limit, Integer offset);
	
	/**
	 * Get beds by location uuid and/or bed type name and/or status
	 *
	 * @param locationUuid {@link String} location Uuid, if null location uuid criteria will not set
	 * @param bedTypeName {@link String} bedTypeName, if null bedTypeName criteria will not set
	 * @param status {@link BedStatus} bed status, if null status criteria will not set
	 * @param limit {@link Integer} limit result set, return all result set if limit is null
	 * @param offset {@link Integer} specify the starting row offset into the result set
	 * @return {@link List<Bed>}
	 */
	@Authorized(value = { "Get Beds" }, requireAll = true)
	List<Bed> getBeds(String locationUuid, String bedTypeName, BedStatus status, Integer limit, Integer offset);
	
	/**
	 * Get bed by bed uuid
	 *
	 * @param uuid {@link String} bed uuid
	 * @return {@link Bed}
	 */
	@Authorized(value = { "Get Beds" }, requireAll = true)
	Bed getBedByUuid(String uuid);
	
	/**
	 * Soft delete bed
	 *
	 * @param bed {@link Bed}
	 * @param reason {@link String} reason of bed delete
	 */
	@Authorized(value = { "Edit Beds" }, requireAll = true)
	void deleteBed(Bed bed, String reason) throws BedOccupiedException;
	
	/**
	 * Save / update bed
	 *
	 * @param bed {@link Bed}
	 * @return {@link Bed}
	 */
	@Authorized(value = { "Edit Beds" }, requireAll = true)
	Bed saveBed(Bed bed);
	
	/**
	 * Get bed tag by Id
	 *
	 * @param uuid {@link String} Bed tag uuid
	 * @return {@link BedTag}
	 */
	@Authorized(value = { "Get Bed Tags" }, requireAll = true)
	BedTag getBedTagByUuid(String uuid);
	
	/**
	 * Get bed tags
	 *
	 * @param name {@link String} bed tag name, if null bed tag name criteria will not set
	 * @param limit {@link Integer} limit result set, return all result set if limit is null
	 * @param offset {@link Integer} specify the starting row offset into the result set
	 * @return {@link List<BedTag>}
	 */
	@Authorized(value = { "Get Bed Tags" }, requireAll = true)
	List<BedTag> getBedTags(String name, Integer limit, Integer offset);
	
	/**
	 * Save / Update bed Tag
	 *
	 * @param bedTag {@link BedTag}
	 * @return {@link BedTag}
	 */
	@Authorized(value = { "Edit Bed Tags" }, requireAll = true)
	BedTag saveBedTag(BedTag bedTag);
	
	/**
	 * Delete bed tag
	 *
	 * @param bedTag {@link BedTag}
	 * @param reason {@link String}
	 */
	@Authorized(value = { "Edit Bed Tags" }, requireAll = true)
	void deleteBedTag(BedTag bedTag, String reason);
	
	/**
	 * Save / Update bed location mapping
	 *
	 * @param bedLocationMapping {@link BedLocationMapping}
	 * @return {@link BedLocationMapping}
	 */
	@Authorized(value = "Edit Beds", requireAll = true)
	BedLocationMapping saveBedLocationMapping(BedLocationMapping bedLocationMapping);
	
	/**
	 * Get bed types by name
	 *
	 * @param name {@link String} bed type name, if null bed type name criteria will not set
	 * @param limit {@link Integer} limit result set, return all result set if limit is null
	 * @param offset {@link Integer} specify the starting row offset into the result set
	 * @return {@link List<BedType>}
	 */
	@Authorized(value = { "Get Bed Type" }, requireAll = true)
	List<BedType> getBedTypes(String name, Integer limit, Integer offset);
	
	/**
	 * Get bed location mapping by location uuid and row and column
	 *
	 * @param locationUuid {@link String} location Uuid
	 * @param row {@link Integer} bed row
	 * @param column {@link Integer} bed column
	 * @return {@link BedLocationMapping}
	 */
	@Authorized(value = "Get Beds", requireAll = true)
	BedLocationMapping getBedLocationMappingByLocationUuidAndRowColumn(String locationUuid, Integer row, Integer column);
	
	/**
	 * Get bedType by Id
	 *
	 * @param uuid {@link Integer} Bed type uuid
	 * @return {@link BedType}
	 */
	@Authorized(value = { "Get Bed Type" }, requireAll = true)
	BedType getBedTypeByUuid(String uuid);
	
	/**
	 * Save / Update bed Type
	 *
	 * @param bedType {@link BedType}
	 * @return {@link BedType}
	 */
	@Authorized(value = { "Edit Bed Type" }, requireAll = true)
	BedType saveBedType(BedType bedType);
	
	/**
	 * Delete bed type
	 *
	 * @param bedType {@link BedType}
	 */
	@Authorized(value = { "Edit Bed Type" }, requireAll = true)
	void deleteBedType(BedType bedType);
}
