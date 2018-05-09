package org.openmrs.module.bedmanagement;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {
        "classpath:TestingApplicationContext.xml" }, inheritLocations = true)
public class BedManagementServiceTest extends BaseModuleWebContextSensitiveTest {
	
	private String superUser;
	
	private String superUserPassword;
	
	private String normalUser;
	
	private String normalUserPassword;
	
	private Patient patient;
	
	private Location location;
	
	private Encounter encounter;
	
	private String bedNumber;
	
	@Autowired
	private LocationService locationService;
	
	@Before
	public void setUp() throws Exception {
		superUser = "test-user";
		superUserPassword = "test";
		normalUser = "normal-user";
		normalUserPassword = "normal-password";
		executeDataSet("bedManagementDAOComponentTestDataset.xml");
		patient = Context.getPatientService().getPatient(3);
		location = Context.getLocationService().getLocation(12347);
		encounter = Context.getEncounterService().getEncounter(2);
		bedNumber = "11";
	}
	
	@Test
	public void shouldPassIfUserHasGetAdmissionLocationsPrivilege() {
		Context.authenticate(superUser, superUserPassword);
		
		BedManagementService bedManagementService = Context.getService(BedManagementService.class);
		
		assertNotNull(bedManagementService.getAdmissionLocations());
		assertNotNull(bedManagementService.getAdmissionLocationByLocation(location));
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveGetAdmissionLocationsPrivilege() {
		Context.authenticate(normalUser, normalUserPassword);
		
		BedManagementService bedManagementService = Context.getService(BedManagementService.class);
		
		bedManagementService.getAdmissionLocations();
		bedManagementService.getAdmissionLocationByLocation(location);
	}
	
	@Test
	public void shouldPassIfUserHasAssignBedsAndEditAdmissionLocationsPrivileges() {
		Context.authenticate(superUser, superUserPassword);
		
		BedManagementService bedManagementService = Context.getService(BedManagementService.class);
		
		assertNotNull(bedManagementService.assignPatientToBed(patient, encounter, bedNumber));
		assertNotNull(bedManagementService.unAssignPatientFromBed(patient));
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveAssignBedsAndEditAdmissionLocationsPrivileges() {
		Context.authenticate(normalUser, normalUserPassword);
		
		BedManagementService bedManagementService = Context.getService(BedManagementService.class);
		
		bedManagementService.assignPatientToBed(patient, encounter, bedNumber);
		bedManagementService.unAssignPatientFromBed(patient);
	}
	
	@Test
	public void shouldPassIfUserHasGetBedsAndEditAdmissionLocationsPrivileges() {
		Context.authenticate(superUser, superUserPassword);
		
		BedManagementService bedManagementService = Context.getService(BedManagementService.class);
		
		assertNotNull(bedManagementService.getBedAssignmentDetailsByPatient(patient));
		assertNotNull(bedManagementService.getBedDetailsById("12"));
		assertNotNull(bedManagementService.getBedDetailsByUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb"));
		assertNotNull(bedManagementService.getBedPatientAssignmentByUuid("7819d653-393b-4118-9c83-a3715b82d4dd"));
		assertNotNull(bedManagementService.getLatestBedDetailsByVisit("8cfda6ae-6b78-11e0-93c3-18a905e044dc"));
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveGetBedsAndEditAdmissionLocationsPrivileges() {
		Context.authenticate(normalUser, normalUserPassword);
		
		BedManagementService bedManagementService = Context.getService(BedManagementService.class);
		
		bedManagementService.getBedAssignmentDetailsByPatient(patient);
		bedManagementService.getBedDetailsById("13");
		bedManagementService.getBedDetailsByUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb");
		bedManagementService.getBedPatientAssignmentByUuid("7819d653-393b-4118-9c83-a3715b82d4dd");
		bedManagementService.getLatestBedDetailsByVisit("8cfda6ae-6b78-11e0-93c3-18a905e044dc");
	}
	
	@Test
	public void shouldSetBedLayoutForAdmissionLocation() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		Location location = Context.getService(LocationService.class)
		        .getLocationByUuid("e26cea2c-1b9f-666e-6511-f3ef6c88af6f");
		AdmissionLocation admissionLocation = Context.getService(BedManagementService.class)
		        .getAdmissionLocationByLocation(location);
		Context.getService(BedManagementService.class).setBedLayoutForAdmissionLocation(admissionLocation, 2, 3);
		List<BedLocationMapping> bedLocationMappings = Context.getService(BedManagementService.class)
		        .getBedLocationMappingsByLocation(admissionLocation.getWard());
		
		Assert.assertEquals(6, bedLocationMappings.size());
		Assert.assertEquals(1, bedLocationMappings.get(0).getRow());
		Assert.assertEquals(1, bedLocationMappings.get(0).getColumn());
		Assert.assertEquals(2, bedLocationMappings.get(5).getRow());
		Assert.assertEquals(3, bedLocationMappings.get(5).getColumn());
	}
	
	@Test
	public void shouldReturnBedLocationMappingByLocation() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		Location location = Context.getService(LocationService.class)
		        .getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
		List<BedLocationMapping> bedLocationMappingList = Context.getService(BedManagementService.class)
		        .getBedLocationMappingsByLocation(location);
		
		Assert.assertEquals(6, bedLocationMappingList.size());
		Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56", bedLocationMappingList.get(0).getLocation().getUuid());
		Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56", bedLocationMappingList.get(5).getLocation().getUuid());
		
		Location location2 = Context.getService(LocationService.class)
		        .getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
		List<BedLocationMapping> bedLocationMappingList2 = Context.getService(BedManagementService.class)
		        .getBedLocationMappingsByLocation(location2);
		Assert.assertEquals(18, bedLocationMappingList2.size());
		Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMappingList2.get(0).getLocation().getUuid());
		Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMappingList2.get(5).getLocation().getUuid());
		Assert.assertNull(bedLocationMappingList2.get(5).getBed());
	}
	
	@Test
	public void shouldSaveAdmissionLocation() {
		Context.authenticate(superUser, superUserPassword);
		Context.addProxyPrivilege("Get Location Attribute Types");
		
		Location location = Context.getLocationService().getLocationByUuid("19e023e8-20ee-4237-ade6-9e68f897b7a9");
		AdmissionLocation admissionLocation = Context.getService(BedManagementService.class)
		        .getAdmissionLocationByLocation(location);
		admissionLocation.setWard(location);
		admissionLocation.getWard().setName("Test ward");
		admissionLocation.getWard().setDescription("For test");
		Context.getService(BedManagementService.class).saveAdmissionLocation(admissionLocation);
		
		Assert.assertEquals("19e023e8-20ee-4237-ade6-9e68f897b7a9", admissionLocation.getWard().getUuid());
		Assert.assertEquals(6, admissionLocation.getTotalBeds());
		Assert.assertNotNull(admissionLocation.getBedLayouts());
	}
	
	@Test
	public void shouldReturnAllBeds() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		List<Bed> allBeds = Context.getService(BedManagementService.class).getBeds(null, null);
		Assert.assertEquals(17, allBeds.size());
		
		List<Bed> BedsWithLimit = Context.getService(BedManagementService.class).getBeds(10, 0);
		Assert.assertEquals(10, BedsWithLimit.size());
	}
	
	@Test
	public void shouldReturnBedsByLocationUuidAndBedTypeNameAndStatus() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		List<Bed> beds = Context.getService(BedManagementService.class).getBeds("98bc9b32-9d1a-11e2-8137-0800271c1b75",
		    "luxury", BedStatus.AVAILABLE, 10, 0);
		
		Assert.assertEquals(2, beds.size());
		Assert.assertEquals("luxury", beds.get(0).getBedType().getName());
		Assert.assertEquals("AVAILABLE", beds.get(0).getStatus());
		Assert.assertEquals("bb049d6d-d225-11e4-9c67-080027b662fc", beds.get(0).getUuid());
		
		Assert.assertEquals("luxury", beds.get(1).getBedType().getName());
		Assert.assertEquals("AVAILABLE", beds.get(1).getStatus());
		Assert.assertEquals("bb0906fa-d225-11e4-9c67-080027b662gh", beds.get(1).getUuid());
	}
	
	@Test
	public void shouldReturnBedsByLocationUuidAndStatus() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		List<Bed> beds = Context.getService(BedManagementService.class).getBeds("98bc9b32-9d1a-11e2-8137-0800271c1b75", null,
		    BedStatus.AVAILABLE, 10, 0);
		
		Assert.assertEquals(9, beds.size());
		Assert.assertEquals("bb049d6d-d225-11e4-9c67-080027b662fc", beds.get(0).getUuid());
		Assert.assertEquals("AVAILABLE", beds.get(0).getStatus());
		
		Assert.assertEquals("AVAILABLE", beds.get(4).getStatus());
		Assert.assertEquals("bb09cacd-d225-11e4-9c67-080027b662sc", beds.get(4).getUuid());
		
		Assert.assertEquals("AVAILABLE", beds.get(8).getStatus());
		Assert.assertEquals("bb0f8866-d225-11e4-9c67-080027b662ec", beds.get(8).getUuid());
	}
	
	@Test
	public void shouldReturnBedsByLocationUuid() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		List<Bed> beds = Context.getService(BedManagementService.class).getBeds("98bc9b32-9d1a-11e2-8137-0800271c1b75", null,
		    null, 10, 0);
		
		Assert.assertEquals(10, beds.size());
		Assert.assertEquals("bb02b84b-d225-11e4-9c67-080027b662ec", beds.get(0).getUuid());
		Assert.assertEquals("OCCUPIED", beds.get(0).getStatus());
		
		Assert.assertEquals("AVAILABLE", beds.get(5).getStatus());
		Assert.assertEquals("bb09cacd-d225-11e4-9c67-080027b662sc", beds.get(5).getUuid());
		
		Assert.assertFalse(beds.get(9).getVoided());
		Assert.assertEquals("bb0f8866-d225-11e4-9c67-080027b662ec", beds.get(9).getUuid());
		
	}
	
	@Test
	public void shouldSoftDeleteBedIfUserHasEditBedsPrivileges() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		Bed bed = Context.getService(BedManagementService.class).getBedById(2);
		Context.getService(BedManagementService.class).deleteBed(bed, "remove bed form location");
		
		Assert.assertTrue(bed.getVoided());
		Assert.assertEquals("remove bed form location", bed.getVoidReason());
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void shouldThrowExceptionSoftDeleteBedIfUserHasNotEditBedsPrivileges() throws Exception {
		Context.authenticate(normalUser, normalUserPassword);
		
		Bed bed = Context.getService(BedManagementService.class).getBedById(1);
		Context.getService(BedManagementService.class).deleteBed(bed, "remove bed form location");
	}
	
	@Test
	public void shouldSaveBedLocationMappingIfUserHasEditBedsPrivileges() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		Bed bed = Context.getService(BedManagementService.class).getBedById(1);
		Location location = Context.getService(LocationService.class)
		        .getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
		BedLocationMapping bedLocationMapping = new BedLocationMapping();
		bedLocationMapping.setBed(bed);
		bedLocationMapping.setLocation(location);
		bedLocationMapping.setRow(4);
		bedLocationMapping.setColumn(1);
		Context.getService(BedManagementService.class).saveBedLocationMapping(bedLocationMapping);
		
		Assert.assertNotNull(bedLocationMapping);
		Assert.assertEquals(4, bedLocationMapping.getRow());
		Assert.assertEquals(1, bedLocationMapping.getColumn());
		Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMapping.getLocation().getUuid());
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void shouldThrowSaveBedLocationMappingIfUserNotHasEditBedsPrivileges() throws Exception {
		Context.authenticate(normalUser, normalUserPassword);
		
		Bed bed = Context.getService(BedManagementService.class).getBedById(1);
		Location location = Context.getService(LocationService.class)
		        .getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
		BedLocationMapping bedLocationMapping = new BedLocationMapping();
		bedLocationMapping.setBed(bed);
		bedLocationMapping.setLocation(location);
		bedLocationMapping.setRow(1);
		bedLocationMapping.setColumn(1);
		Context.getService(BedManagementService.class).saveBedLocationMapping(bedLocationMapping);
		
		Assert.assertNotNull(bedLocationMapping);
		Assert.assertEquals(1, bedLocationMapping.getRow());
		Assert.assertEquals(1, bedLocationMapping.getColumn());
		Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMapping.getLocation().getUuid());
	}
	
	@Test
	public void shouldReturnBedTagByUuid() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		BedTag bedTag = Context.getService(BedManagementService.class)
		        .getBedTagByUuid("73e846d6-ed5f-33e6-a3c9-0800274a5156");
		
		Assert.assertNotNull(bedTag);
		Assert.assertEquals("73e846d6-ed5f-33e6-a3c9-0800274a5156", bedTag.getUuid());
	}
	
	@Test
	public void shouldReturnBedTags() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		List<BedTag> allBedTags = Context.getService(BedManagementService.class).getBedTags(null, 10, 0);
		
		Assert.assertEquals(4, allBedTags.size());
		Assert.assertFalse(allBedTags.get(0).getVoided());
		
		List<BedTag> bedTags = Context.getService(BedManagementService.class).getBedTags("Broken", 10, 0);
		Assert.assertEquals(1, bedTags.size());
		Assert.assertFalse(bedTags.get(0).getVoided());
		Assert.assertEquals("Broken", bedTags.get(0).getName());
	}
	
	@Test
	public void shouldAddNewBedTagIfUserHasEditTagsPrivileges() {
		Context.authenticate(superUser, superUserPassword);
		
		BedTag bedTag = new BedTag();
		bedTag.setName("Reserved");
		Context.getService(BedManagementService.class).saveBedTag(bedTag);
		
		Assert.assertNotNull(bedTag.getId());
		Assert.assertNotEquals("", bedTag.getUuid());
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void shouldThorwExceptionOnAddNewBedTagIfUserNotHasEditTagsPrivileges() {
		Context.authenticate(normalUser, normalUserPassword);
		
		BedTag bedTag = new BedTag();
		bedTag.setName("Reserved");
		Context.getService(BedManagementService.class).saveBedTag(bedTag);
	}
	
	@Test
	public void shouldSoftDeleteBedTagIfUserHasEditTagsPrivileges() {
		Context.authenticate(superUser, superUserPassword);
		
		BedTag bedTag = Context.getService(BedManagementService.class)
		        .getBedTagByUuid("73e846d6-ed5f-22e6-a3c9-0800274a5156");
		Context.getService(BedManagementService.class).deleteBedTag(bedTag, "Not needed more");
		
		Assert.assertEquals("Not needed more", bedTag.getVoidReason());
		Assert.assertTrue(bedTag.getVoided());
	}
	
	@Test
	public void shouldResizeAdmissionLocationBedLayoutIfUserHasPrivileges() {
		Context.authenticate(superUser, superUserPassword);
		
		BedManagementService bedManagementService = Context.getService(BedManagementService.class);
		Location location = Context.getService(LocationService.class)
		        .getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
		AdmissionLocation admissionLocation = bedManagementService.getAdmissionLocationByLocation(location);
		bedManagementService.setBedLayoutForAdmissionLocation(admissionLocation, 3, 5);
		List<BedLocationMapping> bedLocationMappings = bedManagementService.getBedLocationMappingsByLocation(location);
		
		Assert.assertEquals(15, bedLocationMappings.size());
		Assert.assertEquals(1, bedLocationMappings.get(0).getRow());
		Assert.assertEquals(1, bedLocationMappings.get(0).getColumn());
		Assert.assertEquals(3, bedLocationMappings.get(14).getRow());
		Assert.assertEquals(5, bedLocationMappings.get(14).getColumn());
	}
	
	@Test(expected = IllegalPropertyException.class)
	public void shouldThrowExceptionOnResizeBedLayoutIfBlockByExistingBeds() {
		Context.authenticate(superUser, superUserPassword);
		
		BedManagementService bedManagementService = Context.getService(BedManagementService.class);
		Location location = Context.getService(LocationService.class)
		        .getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
		AdmissionLocation admissionLocation = bedManagementService.getAdmissionLocationByLocation(location);
		bedManagementService.setBedLayoutForAdmissionLocation(admissionLocation, 3, 4);
	}
}
