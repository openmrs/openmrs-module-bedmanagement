package org.openmrs.module.bedmanagement.validator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.ValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BedTagValidatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private Validator bedTagValidator;

    @Autowired
    private BedManagementService bedManagementService;

    @Before
    public void setUp() throws Exception {
       
        List<BedTag> allTags = bedManagementService.getAllBedTags();
        for (BedTag tag : allTags) {
            bedManagementService.deleteBedTag(tag, null);
        }
    }

    @Test
    public void validate_shouldFailValidationIfNameIsNullOrEmptyOrWhitespace() {
        BedTag tag = new BedTag();
        Errors errors;

        tag.setName(null);
        errors = new BindException(tag, "tag");
        bedTagValidator.validate(tag, errors);
        assertTrue(errors.hasFieldErrors("name"));

        tag.setName("");
        errors = new BindException(tag, "tag");
        bedTagValidator.validate(tag, errors);
        assertTrue(errors.hasFieldErrors("name"));

        tag.setName("   ");
        errors = new BindException(tag, "tag");
        bedTagValidator.validate(tag, errors);
        assertTrue(errors.hasFieldErrors("name"));
    }

    @Test
    public void validate_shouldFailValidationIfBedTagNameAlreadyInUse() {
        BedTag existing = new BedTag();
        existing.setName("VIP");
        bedManagementService.saveBedTag(existing);

        BedTag duplicate = new BedTag();
        duplicate.setName("VIP");

        Errors errors = new BindException(duplicate, "tag");
        bedTagValidator.validate(duplicate, errors);
        assertTrue(errors.hasFieldErrors("name"));
    }

    @Test
    public void validate_shouldPassValidationIfExistingTagWithSameNameIsVoided() {
        BedTag existing = new BedTag();
        existing.setName("Emergency");
        existing.setVoided(true);
        bedManagementService.saveBedTag(existing);

        BedTag newTag = new BedTag();
        newTag.setName("Emergency");

        Errors errors = new BindException(newTag, "tag");
        bedTagValidator.validate(newTag, errors);
        assertFalse(errors.hasFieldErrors("name"));
    }

    @Test
    public void validate_shouldFailValidationIfDuplicateNameCaseInsensitive() {
        BedTag existing = new BedTag();
        existing.setName("ICU");
        bedManagementService.saveBedTag(existing);

        BedTag duplicate = new BedTag();
        duplicate.setName("icu");

        Errors errors = new BindException(duplicate, "tag");
        bedTagValidator.validate(duplicate, errors);
        assertTrue(errors.hasFieldErrors("name"));
    }

    @Test
    public void validate_shouldPassValidationIfAllRequiredFieldsHaveProperValues() {
        BedTag tag = new BedTag();
        tag.setName("Isolation");

        Errors errors = new BindException(tag, "tag");
        bedTagValidator.validate(tag, errors);
        assertFalse(errors.hasErrors());

        bedManagementService.saveBedTag(tag);
        List<BedTag> tags = bedManagementService.getAllBedTags();
        assertTrue(tags.stream().anyMatch(t -> "Isolation".equals(t.getName())));
    }

    @Test
    public void validate_shouldFailValidationIfFieldLengthIsTooLong() {
        BedTag tag = new BedTag();
        tag.setName("This name is far too long for a bed tag and should definitely fail validation");

        Errors errors = new BindException(tag, "tag");
        bedTagValidator.validate(tag, errors);
        assertTrue(errors.hasFieldErrors("name"));
    }

    @Test
    public void validate_shouldPassValidationIfFieldLengthIsWithinLimit() {
        BedTag tag = new BedTag();
        tag.setName("General Ward");

        Errors errors = new BindException(tag, "tag");
        bedTagValidator.validate(tag, errors);
        assertFalse(errors.hasErrors());
    }

   @Test(expected = ValidationException.class)
	public void saveBedTag_shouldNotSaveInvalidTag() {
		BedTag invalidTag = new BedTag();
		invalidTag.setName("");
        bedManagementService.saveBedTag(invalidTag);
	}

    @Test
    public void saveBedTag_shouldInvokeValidatorIndirectly() {
        BedTag invalidTag = new BedTag();
        invalidTag.setName("");

        try {
            bedManagementService.saveBedTag(invalidTag);
        } catch (Exception ignored) {
        }

        List<BedTag> tags = bedManagementService.getAllBedTags();
        assertFalse("Invalid tag should not be saved", tags.stream().anyMatch(t -> "".equals(t.getName())));
    }
}
