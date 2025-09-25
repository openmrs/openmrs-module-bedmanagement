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
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Objects;

/**
 * Validates attributes on the {@link BedTag} object. Ensures that a bed tag name is unique unless
 * an existing bed tag with the same name is expired.
 *
 * @since 2.x
 */
@Handler(supports = { BedTag.class }, order = 50)
public class BedTagValidator implements Validator {
	
	private final BedManagementService bedManagementService;
	
	// Constructor injection for easier testing
	public BedTagValidator(BedManagementService bedManagementService) {
		this.bedManagementService = bedManagementService;
	}
	
	@Override
	public boolean supports(Class<?> c) {
		return BedTag.class.equals(c);
	}
	
	@Override
	public void validate(Object obj, Errors errors) {
		if (!(obj instanceof BedTag)) {
			errors.reject("error.general");
			return;
		}
		BedTag tag = (BedTag) obj;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.name");
		if (tag.getName() == null || tag.getName().trim().isEmpty()) {
			return;
		}
		
		List<BedTag> allTags = (List<BedTag>) bedManagementService.getAllBedTags();
		
		BedTag existingTag = allTags.stream().filter(t -> t.getName().equalsIgnoreCase(tag.getName())).findFirst()
		        .orElse(null);
		
		if (existingTag != null && !Objects.equals(existingTag.getUuid(), tag.getUuid())) {
			if (existingTag.getDateVoided() == null) {
				errors.rejectValue("name", "general.error.nameAlreadyInUse");
			}
		}
		
		// This may call OpenMRS services, so in tests we will mock this
		ValidateUtil.validateFieldLengths(errors, tag.getClass(), "name");
	}
}
