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

import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.annotation.Handler;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Handler(supports = BedPatientAssignment.class)
public class BedPatientAssignmentValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> clazz) {
		return BedPatientAssignment.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		if (!(target instanceof BedPatientAssignment)) {
			throw new IllegalArgumentException("the parameter target must be of type " + BedPatientAssignment.class);
		}
		BedPatientAssignment bpa = (BedPatientAssignment) target;
		Date bedAssignmentStartTime = bpa.getStartDatetime();
		Date bedAssignmentEndTime = bpa.getEndDatetime();
		Encounter assigningEncounter = bpa.getEncounter();
		Visit visit = assigningEncounter.getVisit();
		
		if (bedAssignmentEndTime != null && bedAssignmentEndTime.before(bedAssignmentStartTime)) {
			errors.rejectValue("startDatetime", "bedPatientAssignment.startDatetime.beforeStartDatetime",
			    "Bed assignment's startDatetime cannot be after endDatetime");
		}
		if (!bedAssignmentStartTime.equals(assigningEncounter.getEncounterDatetime())) {
			errors.rejectValue("startDatetime", "bedPatientAssignment.startDatetime.notEqualToEncounterDatetime",
			    "Bed assignment's endDatetime must be same as encounter datetime");
		}
		if (visit != null && visit.getStopDatetime() != null && bedAssignmentEndTime != null
		        && bedAssignmentEndTime.after(visit.getStopDatetime())) {
			
			errors.rejectValue("endDatetime", "bedPatientAssignment.endDatetime.afterVisitStopDatetime",
			    "Bed assignment's endDatetime cannot be after visit endDatetime");
		}
	}
	
}
