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
package org.openmrs.module.bedmanagement.aop;

import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Handler(supports = Encounter.class)
public class EncounterWithBedPatientAssignmentValidator implements Validator {
	
	private final BedManagementService bedManagementService;
	
	@Autowired
	public EncounterWithBedPatientAssignmentValidator(
	    @Qualifier("bedManagementService") BedManagementService bedManagementService) {
		this.bedManagementService = bedManagementService;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Encounter.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		if (!(target instanceof Encounter)) {
			throw new IllegalArgumentException("the parameter target must be of type " + Encounter.class);
		}
		
		Encounter encounter = (Encounter) target;
		List<BedPatientAssignment> bpaList = bedManagementService.getBedPatientAssignmentByEncounter(encounter.getUuid(),
		    true);
		Date encounterDatetime = encounter.getEncounterDatetime();
		if (encounterDatetime != null) {
			for (BedPatientAssignment bpa : bpaList) {
				if (bpa.getStartDatetime() != null && encounterDatetime.after(bpa.getStartDatetime())) {
					errors.rejectValue("encounterDatetime",
					    "bedPatientAssignment.encounter.encounterDatetimeAfterStartDatetime",
					    "Encounter time cannot be after bed assignment(s) start time");
					break;
				}
			}
		}
	}
}
