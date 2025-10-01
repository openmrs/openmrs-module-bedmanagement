/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.validator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Handler(supports = BedTag.class, order = 50)
public class BedTagValidator implements Validator {
	
	private final BedManagementService bedManagementService;
	
	public BedTagValidator(BedManagementService bedManagementService) {
		this.bedManagementService = bedManagementService;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return BedTag.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		if (!(target instanceof BedTag)) {
			errors.reject("bedtag.invalid");
			return;
		}
		
		BedTag tag = (BedTag) target;
		
		if (tag.getName() == null || tag.getName().trim().isEmpty()) {
			errors.rejectValue("name", "bedtag.name.required");
		}
		
		if (tag.getName() != null && tag.getName().length() > 50) {
			errors.rejectValue("name", "bedtag.name.length");
		}
		
		List<BedTag> existingTags = bedManagementService.getAllBedTags();
		for (BedTag existingTag : existingTags) {
			boolean isVoided = existingTag.getVoided();
			if (!isVoided && existingTag.getName().equals(tag.getName())) {
				errors.rejectValue("name", "bedtag.name.exists");
			}
		}
	}
}
