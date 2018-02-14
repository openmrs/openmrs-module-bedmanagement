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
package org.openmrs.module.bedmanagement.dao;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.bedmanagement.BedLayout;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.entity.*;
import org.openmrs.module.bedmanagement.AdmissionLocation;
import org.openmrs.module.bedmanagement.BedDetails;

import java.util.List;

public interface BedManagementDao {
	
	BedDetails assignPatientToBed(Patient patient, Encounter encounter, Bed bed);
	
	Bed getBedById(int id);
	
	Bed getBedByUuid(String uuid);
	
	Bed getBedByPatient(Patient patient);
	
	Location getWardForBed(Bed bed);
	
	BedDetails unassignPatient(Patient patient, Bed bed);
	
	BedPatientAssignment getBedPatientAssignmentByUuid(String uuid);
	
	List<BedPatientAssignment> getCurrentAssignmentsByBed(Bed bed);
	
	Bed getLatestBedByVisit(String visitUuid);
	
	List<BedTag> getAllBedTags();
	
	/**
	 * Get all admission locations
	 * 
	 * @param locations {@link List<Location>}
	 * @return {@link List<AdmissionLocation>}
	 */
	List<AdmissionLocation> getAdmissionLocations(List<Location> locations);
	
	/**
	 * Get admission location for for a given location. Locations marked with the appropriate tag are
	 * <i>admission</i> locations. This method returns the admission location, if applicable, when given
	 * a candidate location.
	 *
	 * @param location {@link Location}
	 * @return {@link AdmissionLocation} return null if not exist
	 */
	AdmissionLocation getAdmissionLocationForLocation(Location location);
	
	/**
	 * Get bed location mappings by location
	 *
	 * @param location {@link Location}
	 * @return {@link List< BedLocationMapping >}
	 */
	List<BedLocationMapping> getBedLocationMappingByLocation(Location location);
	
	/**
	 * Get bed location mapping by location and row and column
	 *
	 * @param location {@link Location} ward's room location
	 * @param row {@link Integer} bed row
	 * @param column {@link Integer} bed column
	 * @return {@link BedLocationMapping}
	 */
	BedLocationMapping getBedLocationMappingByLocationAndRowAndColumn(Location location, Integer row, Integer column);
	
	/**
	 * Save / Update Bed location mapping
	 *
	 * @param bedLocationMapping {@link BedLocationMapping}
	 * @return {@link BedLocationMapping}
	 */
	BedLocationMapping saveBedLocationMapping(BedLocationMapping bedLocationMapping);
	
	/**
	 * Get bed layout by location
	 *
	 * @param location {@link Location}
	 * @return {@link List<BedLayout>}
	 */
	List<BedLayout> getBedLayoutByLocation(Location location);
	
	/**
	 * Get bed location mapping {@link BedLocationMapping} by bed
	 *
	 * @param bed {@link Bed} bed
	 * @return {@link BedLocationMapping}
	 */
	BedLocationMapping getBedLocationMappingByBed(Bed bed);
	
	/**
	 * Get beds.
	 *
	 * @param location {@link Location} admission location, if null filter by location criteria will not
	 *            applied
	 * @param bedType {@link BedType} bed type, if null filter by bed type criteria will not applied
	 * @param status {@link BedStatus} bed status, if filter by null bed type criteria will not applied
	 * @param limit {@link Integer} limit result set, return all result set if limit is null
	 * @param offset {@link Integer} specify the starting row offset into the result set
	 * @return {@link List<Bed>}
	 */
	List<Bed> getBeds(Location location, BedType bedType, BedStatus status, Integer limit, Integer offset);
	
	/**
	 * Get total bed number by location {@link Location} uuid
	 *
	 * @param location {@link Location} ward's room location
	 * @return {@link Long} total number of beds
	 */
	Integer getBedCountByLocation(Location location);
	
	/**
	 * Save / update bed
	 *
	 * @param bed {@link Bed}
	 * @return {@link Bed}
	 */
	Bed saveBed(Bed bed);
	
	/**
	 * Get bed tag by Uuid
	 *
	 * @param uuid {@link String} bed tag uuid
	 * @return {@link BedTag}
	 */
	BedTag getBedTagByUuid(String uuid);
	
	/**
	 * Return bed tags
	 *
	 * @param name {@link String} bed tag name, if null filter by name criteria will not applied
	 * @param limit {@link Integer} limit result set, return all result set if limit is null
	 * @param offset {@link Integer} specify the starting row offset into the result set
	 * @return {@link List<BedTag>}
	 */
	List<BedTag> getBedTags(String name, Integer limit, Integer offset);
	
	/**
	 * Save / Update bed tag
	 *
	 * @param bedTag {@link BedTag}
	 * @return {@link BedTag}
	 */
	BedTag saveBedTag(BedTag bedTag);
	
	/**
	 * Delete bed tag
	 *
	 * @param bedTag {@link BedTag}
	 */
	void deleteBedTag(BedTag bedTag);
	
	/**
	 * Get bed type by Id
	 * 
	 * @param id {@link Integer} bed Id
	 * @return {@link BedType}
	 */
	BedType getBedTypeById(Integer id);
	
	/**
	 * Get bed type by Uuid
	 *
	 * @param uuid {@link String} bed Uuid
	 * @return {@link BedType}
	 */
	BedType getBedTypeByUuid(String uuid);
	
	/**
	 * Get bed sypes
	 *
	 * @param name {@link String} bed type name, if null filter by name criteria will not applied
	 * @param limit {@link Integer} limit result set, return all result set if limit is null
	 * @param offset {@link Integer} specify the starting row offset into the result set
	 * @return {@link List<BedType>}
	 */
	List<BedType> getBedTypes(String name, Integer limit, Integer offset);
	
	/**
	 * Save/Update bed type
	 *
	 * @param bedType {@link BedType}
	 * @return {@link BedType}
	 */
	BedType saveBedType(BedType bedType);
	
	/**
	 * Delete Bed type
	 *
	 * @param bedType {@link BedType}
	 */
	void deleteBedType(BedType bedType);
}
