package org.openmrs.module.bedmanagement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.APIException;
import org.openmrs.api.LocationService;
import org.openmrs.api.ValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BedManagementServiceTest extends BaseModuleContextSensitiveTest {
	
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
	
	@BeforeEach
	public void setUp() throws Exception {
		superUser = "test-user";
		superUserPassword = "test";
		normalUser = "normal-user";
		normalUserPassword = "normal-password";
		executeDataSet("testPatientsDataset.xml");
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
	
	@Test
	public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveGetAdmissionLocationsPrivilege() {
		assertThrows(APIAuthenticationException.class, () -> {
			Context.authenticate(normalUser, normalUserPassword);
			
			BedManagementService bedManagementService = Context.getService(BedManagementService.class);
			
			bedManagementService.getAdmissionLocations();
			bedManagementService.getAdmissionLocationByLocation(location);
		});
	}
	
	@Test
	public void shouldPassIfUserHasAssignBedsAndEditAdmissionLocationsPrivileges() {
		Context.authenticate(superUser, superUserPassword);
		
		BedManagementService bedManagementService = Context.getService(BedManagementService.class);
		
		assertNotNull(bedManagementService.assignPatientToBed(patient, encounter, bedNumber));
		Context.flushSession();
		Context.clearSession();
		assertNotNull(bedManagementService.unAssignPatientFromBed(patient));
	}
	
	@Test
	public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveAssignBedsAndEditAdmissionLocationsPrivileges() {
		assertThrows(APIAuthenticationException.class, () -> {
			Context.authenticate(normalUser, normalUserPassword);
			
			BedManagementService bedManagementService = Context.getService(BedManagementService.class);
			
			bedManagementService.assignPatientToBed(patient, encounter, bedNumber);
			bedManagementService.unAssignPatientFromBed(patient);
		});
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
	
	@Test
	public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveGetBedsAndEditAdmissionLocationsPrivileges() {
		assertThrows(APIAuthenticationException.class, () -> {
			Context.authenticate(normalUser, normalUserPassword);
			
			BedManagementService bedManagementService = Context.getService(BedManagementService.class);
			
			bedManagementService.getBedAssignmentDetailsByPatient(patient);
			bedManagementService.getBedDetailsById("13");
			bedManagementService.getBedDetailsByUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb");
			bedManagementService.getBedPatientAssignmentByUuid("7819d653-393b-4118-9c83-a3715b82d4dd");
			bedManagementService.getLatestBedDetailsByVisit("8cfda6ae-6b78-11e0-93c3-18a905e044dc");
		});
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
		
		Assertions.assertEquals(6, bedLocationMappings.size());
		Assertions.assertEquals(1, bedLocationMappings.get(0).getRow());
		Assertions.assertEquals(1, bedLocationMappings.get(0).getColumn());
		Assertions.assertEquals(2, bedLocationMappings.get(5).getRow());
		Assertions.assertEquals(3, bedLocationMappings.get(5).getColumn());
	}
	
	@Test
	public void shouldReturnBedLocationMappingByLocation() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		Location location = Context.getService(LocationService.class)
		        .getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
		List<BedLocationMapping> bedLocationMappingList = Context.getService(BedManagementService.class)
		        .getBedLocationMappingsByLocation(location);
		
		Assertions.assertEquals(6, bedLocationMappingList.size());
		Assertions.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56",
		    bedLocationMappingList.get(0).getLocation().getUuid());
		Assertions.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56",
		    bedLocationMappingList.get(5).getLocation().getUuid());
		
		Location location2 = Context.getService(LocationService.class)
		        .getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
		List<BedLocationMapping> bedLocationMappingList2 = Context.getService(BedManagementService.class)
		        .getBedLocationMappingsByLocation(location2);
		Assertions.assertEquals(18, bedLocationMappingList2.size());
		Assertions.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75",
		    bedLocationMappingList2.get(0).getLocation().getUuid());
		Assertions.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75",
		    bedLocationMappingList2.get(5).getLocation().getUuid());
		Assertions.assertNull(bedLocationMappingList2.get(5).getBed());
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
		
		Assertions.assertEquals("19e023e8-20ee-4237-ade6-9e68f897b7a9", admissionLocation.getWard().getUuid());
		Assertions.assertEquals(6, admissionLocation.getTotalBeds());
		Assertions.assertNotNull(admissionLocation.getBedLayouts());
	}
	
	@Test
	public void shouldReturnAllBeds() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		List<Bed> allBeds = Context.getService(BedManagementService.class).getBeds(null, null);
		Assertions.assertEquals(17, allBeds.size());
		
		List<Bed> BedsWithLimit = Context.getService(BedManagementService.class).getBeds(10, 0);
		Assertions.assertEquals(10, BedsWithLimit.size());
	}
	
	@Test
	public void shouldReturnBedsByLocationUuidAndBedTypeNameAndStatus() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		List<Bed> beds = Context.getService(BedManagementService.class).getBeds("98bc9b32-9d1a-11e2-8137-0800271c1b75",
		    "luxury", BedStatus.AVAILABLE, 10, 0);
		
		Assertions.assertEquals(2, beds.size());
		Assertions.assertEquals("luxury", beds.get(0).getBedType().getName());
		Assertions.assertEquals("AVAILABLE", beds.get(0).getStatus());
		Assertions.assertEquals("bb049d6d-d225-11e4-9c67-080027b662fc", beds.get(0).getUuid());
		
		Assertions.assertEquals("luxury", beds.get(1).getBedType().getName());
		Assertions.assertEquals("AVAILABLE", beds.get(1).getStatus());
		Assertions.assertEquals("bb0906fa-d225-11e4-9c67-080027b662gh", beds.get(1).getUuid());
	}
	
	@Test
	public void shouldReturnBedsByLocationUuidAndStatus() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		List<Bed> beds = Context.getService(BedManagementService.class).getBeds("98bc9b32-9d1a-11e2-8137-0800271c1b75", null,
		    BedStatus.AVAILABLE, 10, 0);
		
		Assertions.assertEquals(9, beds.size());
		Assertions.assertEquals("bb049d6d-d225-11e4-9c67-080027b662fc", beds.get(0).getUuid());
		Assertions.assertEquals("AVAILABLE", beds.get(0).getStatus());
		
		Assertions.assertEquals("AVAILABLE", beds.get(4).getStatus());
		Assertions.assertEquals("bb09cacd-d225-11e4-9c67-080027b662sc", beds.get(4).getUuid());
		
		Assertions.assertEquals("AVAILABLE", beds.get(8).getStatus());
		Assertions.assertEquals("bb0f8866-d225-11e4-9c67-080027b662ec", beds.get(8).getUuid());
	}
	
	@Test
	public void shouldReturnBedsByLocationUuid() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		List<Bed> beds = Context.getService(BedManagementService.class).getBeds("98bc9b32-9d1a-11e2-8137-0800271c1b75", null,
		    null, 10, 0);
		
		Assertions.assertEquals(10, beds.size());
		Assertions.assertEquals("bb02b84b-d225-11e4-9c67-080027b662ec", beds.get(0).getUuid());
		Assertions.assertEquals("OCCUPIED", beds.get(0).getStatus());
		
		Assertions.assertEquals("AVAILABLE", beds.get(5).getStatus());
		Assertions.assertEquals("bb09cacd-d225-11e4-9c67-080027b662sc", beds.get(5).getUuid());
		
		Assertions.assertFalse(beds.get(9).getVoided());
		Assertions.assertEquals("bb0f8866-d225-11e4-9c67-080027b662ec", beds.get(9).getUuid());
		
	}
	
	@Test
	public void shouldSoftDeleteBedIfUserHasEditBedsPrivileges() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		Bed bed = Context.getService(BedManagementService.class).getBedById(2);
		Context.getService(BedManagementService.class).deleteBed(bed, "remove bed form location");
		
		Assertions.assertTrue(bed.getVoided());
		Assertions.assertEquals("remove bed form location", bed.getVoidReason());
	}
	
	@Test
	public void shouldThrowExceptionSoftDeleteBedIfUserHasNotEditBedsPrivileges() throws Exception {
		assertThrows(APIAuthenticationException.class, () -> {
			Context.authenticate(normalUser, normalUserPassword);
			
			Bed bed = Context.getService(BedManagementService.class).getBedById(1);
			Context.getService(BedManagementService.class).deleteBed(bed, "remove bed form location");
		});
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
		
		Assertions.assertNotNull(bedLocationMapping);
		Assertions.assertEquals(4, bedLocationMapping.getRow());
		Assertions.assertEquals(1, bedLocationMapping.getColumn());
		Assertions.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMapping.getLocation().getUuid());
	}
	
	@Test
	public void shouldThrowSaveBedLocationMappingIfUserNotHasEditBedsPrivileges() throws Exception {
		assertThrows(APIAuthenticationException.class, () -> {
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
			
			Assertions.assertNotNull(bedLocationMapping);
			Assertions.assertEquals(1, bedLocationMapping.getRow());
			Assertions.assertEquals(1, bedLocationMapping.getColumn());
			Assertions.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMapping.getLocation().getUuid());
		});
	}
	
	@Test
	public void shouldReturnBedTagByUuid() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		BedTag bedTag = Context.getService(BedManagementService.class)
		        .getBedTagByUuid("73e846d6-ed5f-33e6-a3c9-0800274a5156");
		
		Assertions.assertNotNull(bedTag);
		Assertions.assertEquals("73e846d6-ed5f-33e6-a3c9-0800274a5156", bedTag.getUuid());
	}
	
	@Test
	public void shouldReturnBedTags() throws Exception {
		Context.authenticate(superUser, superUserPassword);
		
		List<BedTag> allBedTags = Context.getService(BedManagementService.class).getBedTags(null, 10, 0);
		
		Assertions.assertEquals(4, allBedTags.size());
		Assertions.assertFalse(allBedTags.get(0).getVoided());
		
		List<BedTag> bedTags = Context.getService(BedManagementService.class).getBedTags("Broken", 10, 0);
		Assertions.assertEquals(1, bedTags.size());
		Assertions.assertFalse(bedTags.get(0).getVoided());
		Assertions.assertEquals("Broken", bedTags.get(0).getName());
	}
	
	@Test
	public void saveBedTag_shouldNotSaveInvalidTag() {
		assertThrows(ValidationException.class, () -> {
			BedTag invalidTag = new BedTag();
			invalidTag.setName("");
			Context.getService(BedManagementService.class).saveBedTag(invalidTag);
		});
	}
	
	@Test
	public void shouldAddNewBedTagIfUserHasEditTagsPrivileges() {
		Context.authenticate(superUser, superUserPassword);
		
		BedTag bedTag = new BedTag();
		bedTag.setName("Reserved");
		Context.getService(BedManagementService.class).saveBedTag(bedTag);
		
		Assertions.assertNotNull(bedTag.getId());
		Assertions.assertNotEquals("", bedTag.getUuid());
	}
	
	@Test
	public void shouldThorwExceptionOnAddNewBedTagIfUserNotHasEditTagsPrivileges() {
		assertThrows(APIAuthenticationException.class, () -> {
			Context.authenticate(normalUser, normalUserPassword);
			
			BedTag bedTag = new BedTag();
			bedTag.setName("Reserved");
			Context.getService(BedManagementService.class).saveBedTag(bedTag);
		});
	}
	
	@Test
	public void shouldSoftDeleteBedTagIfUserHasEditTagsPrivileges() {
		Context.authenticate(superUser, superUserPassword);
		
		BedTag bedTag = Context.getService(BedManagementService.class)
		        .getBedTagByUuid("73e846d6-ed5f-22e6-a3c9-0800274a5156");
		Context.getService(BedManagementService.class).deleteBedTag(bedTag, "Not needed more");
		
		Assertions.assertEquals("Not needed more", bedTag.getVoidReason());
		Assertions.assertTrue(bedTag.getVoided());
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
		
		Assertions.assertEquals(15, bedLocationMappings.size());
		Assertions.assertEquals(1, bedLocationMappings.get(0).getRow());
		Assertions.assertEquals(1, bedLocationMappings.get(0).getColumn());
		Assertions.assertEquals(3, bedLocationMappings.get(14).getRow());
		Assertions.assertEquals(5, bedLocationMappings.get(14).getColumn());
	}
	
	@Test
	public void shouldGetBedPatientAssignmentByPatient() {
		Context.authenticate(superUser, superUserPassword);
		
		BedManagementService bedManagementService = Context.getService(BedManagementService.class);
		
		List<BedPatientAssignment> totalBpaList = bedManagementService.getBedPatientAssignmentByPatient(patient.getUuid(),
		    true);
		Assertions.assertEquals(totalBpaList.size(), 2);
		
		List<BedPatientAssignment> currentBpaList = bedManagementService.getBedPatientAssignmentByPatient(patient.getUuid(),
		    false);
		Assertions.assertEquals(currentBpaList.size(), 1);
	}
	
	@Test
	public void shouldThrowExceptionOnResizeBedLayoutIfBlockByExistingBeds() {
		assertThrows(APIException.class, () -> {
			Context.authenticate(superUser, superUserPassword);
			
			BedManagementService bedManagementService = Context.getService(BedManagementService.class);
			Location location = Context.getService(LocationService.class)
			        .getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
			AdmissionLocation admissionLocation = bedManagementService.getAdmissionLocationByLocation(location);
			bedManagementService.setBedLayoutForAdmissionLocation(admissionLocation, 3, 4);
		});
	}
}
