/**
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
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Handler(supports = { BedTag.class }, order = 50)
public class BedTagValidator implements Validator {

    @Autowired
    private BedManagementService bedManagementService;

    @Override
    public boolean supports(Class<?> clazz) {
        return BedTag.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!(target instanceof BedTag)) {
            errors.reject("error.general");
            return;
        }

        BedTag tag = (BedTag) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.name");

        if (StringUtils.isBlank(tag.getName())) {
            return;
        }

        BedTag existing = bedManagementService.getBedTagByUuid(tag.getName().trim());

        if (existing != null && existing.getDateVoided() == null
                && !existing.getUuid().equals(tag.getUuid())) {
            errors.rejectValue("name", "general.error.nameAlreadyInUse");
        }

        ValidateUtil.validateFieldLengths(errors, tag.getClass(), "name");
    }
}
