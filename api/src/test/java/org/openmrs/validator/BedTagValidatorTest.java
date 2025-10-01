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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class BedTagValidatorTest {

    private BedTagValidator validator;

    private BedManagementService bedManagementService;

    private BedTag activeTag;

    private BedTag expiredTag;

    @Before
    public void setup() {
        bedManagementService = mock(BedManagementService.class);
        validator = new BedTagValidator(bedManagementService);

        activeTag = new BedTag();
        activeTag.setName("ICU-1");
        activeTag.setUuid("active-uuid");
        activeTag.setDateVoided(null);

        expiredTag = new BedTag();
        expiredTag.setName("ExpiredTag");
        expiredTag.setUuid("expired-uuid");
        expiredTag.setDateVoided(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
    }

    @Test
    public void validate_shouldFailIfNameIsNullOrEmptyOrWhitespace() {
        BedTag tag = new BedTag();

        tag.setName(null);
        Errors errors = new BindException(tag, "tag");
        validator.validate(tag, errors);
        assertTrue(errors.hasFieldErrors("name"));

        tag.setName("");
        errors = new BindException(tag, "tag");
        validator.validate(tag, errors);
        assertTrue(errors.hasFieldErrors("name"));

        tag.setName(" ");
        errors = new BindException(tag, "tag");
        validator.validate(tag, errors);
        assertTrue(errors.hasFieldErrors("name"));
    }

    @Test
    public void validate_shouldFailIfNonExpiredNameAlreadyInUse() {
        when(bedManagementService.getAllBedTags()).thenReturn(Arrays.asList(activeTag));

        BedTag tag = new BedTag();
        tag.setName("ICU-1");

        Errors errors = new BindException(tag, "tag");
        validator.validate(tag, errors);

        assertTrue(errors.hasFieldErrors("name"));
    }

    @Test
    public void validate_shouldPassIfExistingNameIsExpired() {
        when(bedManagementService.getAllBedTags()).thenReturn(Arrays.asList(expiredTag));

        BedTag tag = new BedTag();
        tag.setName("ExpiredTag");

        Errors errors = new BindException(tag, "tag");
        validator.validate(tag, errors);

        assertFalse(errors.hasFieldErrors("name"));
    }

    @Test
    public void validate_shouldPassIfAllRequiredFieldsAreValid() {
        when(bedManagementService.getAllBedTags()).thenReturn(Collections.emptyList());

        BedTag tag = new BedTag();
        tag.setName("NewTag");

        Errors errors = new BindException(tag, "tag");
        validator.validate(tag, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_shouldFailIfFieldLengthsAreInvalid() {
        BedTag tag = new BedTag();
        tag.setName("ThisNameIsWayTooLongForABedTagAndShouldFailValidationAccordingToFieldLengthRules");

        Errors errors = new BindException(tag, "tag");
        validator.validate(tag, errors);

        assertTrue(errors.hasFieldErrors("name"));
    }

    @Test
    public void validate_shouldRejectNonBedTagObjects() {
        Object notABedTag = new Object();
        Errors errors = new BindException(notABedTag, "notABedTag");
        validator.validate(notABedTag, errors);

        assertTrue(errors.hasGlobalErrors());
    }

    @Test
    public void saveBedTag_shouldInvokeValidator() {
        BedTagValidator spyValidator = spy(new BedTagValidator(bedManagementService));

        BedManagementService service = mock(BedManagementService.class);

        BedTag tag = new BedTag();
        tag.setName("NewTag");

        doAnswer(invocation -> {
            Errors errors = new BindException(tag, "tag");
            spyValidator.validate(tag, errors);
            return null;
        }).when(service).saveBedTag(tag);

        service.saveBedTag(tag);

        verify(spyValidator, times(1)).validate(eq(tag), any(Errors.class));
    }
}
