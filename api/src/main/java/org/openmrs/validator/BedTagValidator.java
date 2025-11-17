/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc.
 */

package org.openmrs.validator;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.openmrs.util.OpenmrsUtil;

@Component("bedTagValidator")
@Handler(supports = { BedTag.class }, order = 50)
public class BedTagValidator implements Validator {

    private static final int MAX_NAME_LENGTH = 50;

    @Override
    public boolean supports(Class<?> clazz) {
        return BedTag.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!(target instanceof BedTag)) {
            errors.reject("error.general", "Invalid object type");
            return;
        }

        BedTag tag = (BedTag) target;

        if (StringUtils.isBlank(tag.getName())) {
            errors.rejectValue("name", "bedtag.name.required", "Name is required");
            return;
        }

        if (tag.getName().length() > MAX_NAME_LENGTH) {
            errors.rejectValue(
                    "name",
                    "bedtag.name.tooLong",
                    "Name must be at most " + MAX_NAME_LENGTH + " characters"
            );
        }

        BedManagementService bedManagementService = Context.getService(BedManagementService.class);

        for (BedTag existingTag : bedManagementService.getAllBedTags()) {
            if (!existingTag.isVoided()
                    && existingTag.getName() != null
                    && existingTag.getName().equalsIgnoreCase(tag.getName())
                    && !OpenmrsUtil.nullSafeEquals(existingTag.getUuid(), tag.getUuid())) {

                errors.rejectValue("name", "bedtag.name.duplicate", "Bed tag name already exists");
                break;
            }
        }
    }
}
