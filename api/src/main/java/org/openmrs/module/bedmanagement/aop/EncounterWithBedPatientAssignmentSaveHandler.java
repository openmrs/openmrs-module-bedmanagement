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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.handler.SaveHandler;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Handler(supports = Encounter.class)
public class EncounterWithBedPatientAssignmentSaveHandler implements SaveHandler<Encounter> {
	
	private final Log log = LogFactory.getLog(getClass());
	
	private final BedManagementService bedManagementService;
	
	@Autowired
	public EncounterWithBedPatientAssignmentSaveHandler(
	    @Qualifier("bedManagementService") BedManagementService bedManagementService) {
		this.bedManagementService = bedManagementService;
	}
	
	@Override
	public void handle(Encounter encounter, User user, Date date, String s) {
		
		if (encounter.getEncounterId() != null) {
			
			List<BedPatientAssignment> bpaList = bedManagementService.getBedPatientAssignmentByEncounter(encounter.getUuid(),
			    true);
			
			if (encounter.getVoided()) {
				log.debug("Voiding beds due to voided encounter");
				for (BedPatientAssignment bpa : bpaList) {
					bedManagementService.deleteBedPatientAssignment(bpa, "encounter voided");
					log.debug("Voided bedPatientAssignment for bed " + bpa.getBed());
				}
			}
			
			Date encounterDatetime = encounter.getEncounterDatetime();
			for (BedPatientAssignment bpa : bpaList) {
				log.debug("updating bedPatientAssignment starttime for bed " + bpa.getBed());
				bpa.setStartDatetime(encounterDatetime);
				bedManagementService.saveBedPatientAssignment(bpa);
			}
		}
		
	}
}
