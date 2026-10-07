package org.openmrs.module.bedmanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.entity.BedTagMap;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.bedmanagement.service.BedTagMapService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BedTagMapServiceTest extends BaseModuleContextSensitiveTest {
	
	private String privilegedUser;
	
	private String privilegedUserPassword;
	
	private String normalUser;
	
	private String normalUserPassword;
	
	private Bed bedFifteen;
	
	private BedTag isolationBedTag;
	
	private BedTagMap bedTagMap;
	
	private BedTagMapService bedTagMapService;
	
	@BeforeEach
	public void setUp() throws Exception {
		privilegedUser = "edit-tags-user";
		privilegedUserPassword = "normal-password";
		normalUser = "normal-user";
		normalUserPassword = "normal-password";
		executeDataSet("bedTagMapTestDataSet.xml");
		isolationBedTag = Context.getService(BedTagMapService.class).getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f3");
		bedFifteen = Context.getService(BedManagementService.class).getBedById(15);
		bedTagMap = new BedTagMap();
		bedTagMap.setBedTag(isolationBedTag);
		bedTagMap.setBed(bedFifteen);
		bedTagMapService = Context.getService(BedTagMapService.class);
	}
	
	@Test
	public void shouldAssignTheBedTagToBedIfTheUserHasTheGetTagsEditTagsAndGetBedsPrivileges() {
		Context.authenticate(privilegedUser, privilegedUserPassword);
		BedTagMap savedBedTagMap = bedTagMapService.save(bedTagMap);
		
		assertNotNull(savedBedTagMap);
		assertNotNull(savedBedTagMap.getId());
		assertEquals(isolationBedTag, savedBedTagMap.getBedTag());
		assertEquals(bedFifteen, savedBedTagMap.getBed());
	}
	
	@Test
	public void shouldThrowAuthenticationExceptionIfTheUserDoesNotHaveTheGetTagsEditTagsAndGetBedsPrivileges() {
		assertThrows(APIAuthenticationException.class, () -> {
			Context.authenticate(normalUser, normalUserPassword);
			bedTagMapService.save(bedTagMap);
		});
	}
	
	@Test
	public void shouldUnAssignTheBedTagFromTheBedIfTheUserHasTheGetTagsEditTagsAndGetBedsPrivileges() {
		Context.authenticate(privilegedUser, privilegedUserPassword);
		bedTagMapService.delete(bedTagMap, "Need beds in general ward");
	}
	
	@Test
	public void shouldThrowAuthenticationExceptionIfTheUserDoesNotHaveTheGetTagsEditTagsAndGetBedsPrivilegesWhileDeletingTheBedTagMap() {
		assertThrows(APIAuthenticationException.class, () -> {
			Context.authenticate(normalUser, normalUserPassword);
			bedTagMapService.delete(bedTagMap, "Need beds in general ward");
		});
	}
	
	@Test
	public void shouldGetBedTagMapByUuidIfTheUserHasTheGetTagsAndGetBedsPrivileges() {
		Context.authenticate(privilegedUser, privilegedUserPassword);
		BedTag oxygenBedTag = bedTagMapService.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
		BedTagMap bedElevenWithOxygenTag = bedTagMapService.getBedTagMapByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f4");
		
		assertNotNull(bedElevenWithOxygenTag);
		assertNotNull(bedElevenWithOxygenTag.getId());
		assertEquals(oxygenBedTag, bedElevenWithOxygenTag.getBedTag());
		assertEquals(bedFifteen, bedElevenWithOxygenTag.getBed());
	}
	
	@Test
	public void shouldThrowAuthenticationExceptionIfTheUserDoesNotHaveTheGetTagsAndGetBedsPrivilegesWhileGettingTheBedTagMapUsingUuid() {
		assertThrows(APIAuthenticationException.class, () -> {
			Context.authenticate(normalUser, normalUserPassword);
			bedTagMapService.getBedTagMapByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f4");
		});
	}
	
	@Test
	public void shouldGetBedTagMapWithBedAndTagIfTheUserHasTheGetTagsAndGetBedsPrivileges() {
		Context.authenticate(privilegedUser, privilegedUserPassword);
		BedTag oxygenBedTag = bedTagMapService.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
		BedTagMap bedTagMapWithBedAndTag = bedTagMapService.getBedTagMapWithBedAndTag(bedFifteen, oxygenBedTag);
		
		assertNotNull(bedTagMapWithBedAndTag);
		assertNotNull(bedTagMapWithBedAndTag.getId());
		assertEquals(oxygenBedTag, bedTagMapWithBedAndTag.getBedTag());
		assertEquals(bedFifteen, bedTagMapWithBedAndTag.getBed());
	}
	
	@Test
	public void shouldThrowAuthenticationExceptionIfTheUserDoesNotHaveTheGetTagsAndGetBedsPrivilegesWhileGettingTheBedTagMapUsingBedAndBedTag() {
		assertThrows(APIAuthenticationException.class, () -> {
			Context.authenticate(normalUser, normalUserPassword);
			BedTag oxygenBedTag = bedTagMapService.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
			bedTagMapService.getBedTagMapWithBedAndTag(bedFifteen, oxygenBedTag);
		});
	}
	
	@Test
	public void shouldGetBedTagByUuidIfTheUserHasTheGetTagsAndGetBedsPrivileges() throws Exception {
		Context.authenticate(privilegedUser, privilegedUserPassword);
		BedTag oxygenBedTag = bedTagMapService.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
		
		assertNotNull(oxygenBedTag);
		assertEquals("Oxygen", oxygenBedTag.getName());
	}
	
	@Test
	public void shouldThrowAuthenticationExceptionIfTheUserDoesNotHaveTheGetTagsAndGetBedsPrivilegesWhileGettingBedTagByUuid()
	        throws Exception {
		assertThrows(APIAuthenticationException.class, () -> {
			Context.authenticate(normalUser, normalUserPassword);
			bedTagMapService.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
		});
	}
}
