package org.openmrs.module.bedmanagement.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BedTagValidatorTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private Validator bedTagValidator;
	
	@Autowired
	private BedManagementService bedManagementService;
	
	@BeforeEach
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
		
		tag.setName(" ");
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
	public void validate_shouldPassValidationIfNameIsValid() {
		BedTag tag = new BedTag();
		tag.setName("Isolation");
		
		Errors errors = new BindException(tag, "tag");
		bedTagValidator.validate(tag, errors);
		
		assertFalse(errors.hasErrors());
	}
	
	@Test
	public void validate_shouldFailValidationIfFieldLengthIsTooLong() {
		BedTag tag = new BedTag();
		String longName = new String(new char[260]).replace('\0', 'A');
		tag.setName(longName);
		
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
}
