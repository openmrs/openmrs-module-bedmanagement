package org.openmrs.module.bedmanagement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.db.LocationDAO;
import org.openmrs.module.bedmanagement.constants.BedManagementApiConstants;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.dao.BedManagementDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;

public class HibernateBedManagementDaoTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	BedManagementDao bedManagementDao;
	
	@Autowired
	LocationDAO locationDao;
	
	@BeforeEach
	public void beforeAllTests() throws Exception {
		executeDataSet("testPatientsDataset.xml");
		executeDataSet("bedManagementDAOComponentTestDataset.xml");
	}
	
	@Test
	public void shouldGetBedDetailsByVisit() throws Exception {
		Bed bed = bedManagementDao.getLatestBedByVisit("8cfda6ae-6b78-11e0-93c3-18a905e044dc");
		assertThat(bed.getId(), is(equalTo(12)));
	}
	
	@Test
	public void shouldReturnNullIfPatientIsNotAssignedToAnyBed() throws Exception {
		Bed bed = bedManagementDao.getLatestBedByVisit("7d8c1980-6b78-11e0-93c3-18a905e044dc");
		assertNull(bed);
	}
	
	@Test
	public void shouldReturnNullIfVisitNotExists() throws Exception {
		Bed bed = bedManagementDao.getLatestBedByVisit("abcd");
		assertNull(bed);
	}
	
	@Test
	public void shouldGetTheLatestBedDetailsForAVisit() throws Exception {
		Bed bed = bedManagementDao.getLatestBedByVisit("e1428fea-6b78-11e0-93c3-18a905e044dc");
		assertThat(bed.getId(), is(equalTo(12)));
	}
	
	@Test
	public void shouldGetTheLatestBedForSameEncounterAndSameVisit() {
		Bed bed = bedManagementDao.getLatestBedByVisit("8cfda6ae-6b78-11e0-93c3-18a905e044dc");
		assertThat(bed.getId(), is(equalTo(12)));
	}
	
	@Test
	public void shouldReturnWardForBed() throws Exception {
		Bed bed = bedManagementDao.getBedByUuid("bb0906fa-d225-11e4-9c67-080027b662ec");
		Location ward = bedManagementDao.getWardForBed(bed);
		
		Assertions.assertNotNull(ward);
		Assertions.assertEquals("Physical Location for Orthopaedic ward", ward.getName());
		Assertions.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56", ward.getUuid());
	}
	
	@Test
	public void shouldReturnAdmissionLocationLayoutByLocation() throws Exception {
		Location location = locationDao.getLocationByUuid("19e023e8-20ee-4237-ade6-9e68f897b7a9");
		List<BedLayout> bedLayouts = bedManagementDao.getBedLayoutsByLocation(location);
		
		Assertions.assertEquals(6, bedLayouts.size());
		Assertions.assertEquals("307-a", bedLayouts.get(0).getBedNumber());
		Assertions.assertEquals("OCCUPIED", bedLayouts.get(0).getStatus());
		Assertions.assertEquals("Physical Location for Orthopaedic ward", bedLayouts.get(0).getLocation());
		Assertions.assertEquals("306-e", bedLayouts.get(5).getBedNumber());
		Assertions.assertEquals("AVAILABLE", bedLayouts.get(5).getStatus());
		Assertions.assertEquals("Physical Location for Orthopaedic ward", bedLayouts.get(5).getLocation());
		
		Location location2 = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
		List<BedLayout> locationBedLayouts = bedManagementDao.getBedLayoutsByLocation(location2);
		Assertions.assertEquals(18, locationBedLayouts.size());
		Assertions.assertEquals("304-a", locationBedLayouts.get(0).getBedNumber());
		Assertions.assertEquals("OCCUPIED", locationBedLayouts.get(0).getStatus());
		Assertions.assertEquals("Physical Location for Cardio ward on first floor", locationBedLayouts.get(0).getLocation());
		Assertions.assertNull(locationBedLayouts.get(4).getBedId());
		Assertions.assertEquals(2, locationBedLayouts.get(4).getRowNumber().intValue());
		Assertions.assertEquals(1, locationBedLayouts.get(4).getColumnNumber().intValue());
	}
	
	@Test
	public void shouldReturnAdmissionLocationByLocation() throws Exception {
		Location location = locationDao.getLocationByUuid("19e023e8-20ee-4237-ade6-9e68f897b7a9");
		AdmissionLocation admissionLocation = bedManagementDao.getAdmissionLocationForLocation(location);
		List<BedLayout> bedLayouts = admissionLocation.getBedLayouts();
		
		Assertions.assertEquals(6, admissionLocation.getTotalBeds());
		Assertions.assertEquals(6, admissionLocation.getBedLayouts().size());
		Assertions.assertEquals(2, admissionLocation.getOccupiedBeds());
		Assertions.assertEquals("19e023e8-20ee-4237-ade6-9e68f897b7a9", admissionLocation.getWard().getUuid());
		Assertions.assertEquals("307-a", bedLayouts.get(0).getBedNumber());
		Assertions.assertEquals("OCCUPIED", bedLayouts.get(0).getStatus());
		Assertions.assertEquals("Physical Location for Orthopaedic ward", bedLayouts.get(0).getLocation());
		Assertions.assertEquals("306-e", bedLayouts.get(5).getBedNumber());
		Assertions.assertEquals("AVAILABLE", bedLayouts.get(5).getStatus());
		Assertions.assertEquals("Physical Location for Orthopaedic ward", bedLayouts.get(5).getLocation());
	}
	
	@Test
	public void shouldReturnAllAdmissionLocations() throws Exception {
		LocationTag admissionLocationTag = locationDao
		        .getLocationTagByName(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION);
		List<Location> locations = new ArrayList<>();
		for (Location l : locationDao.getAllLocations(false)) {
			if (l.getTags().contains(admissionLocationTag)) {
				locations.add(l);
			}
		}
		
		List<AdmissionLocation> admissionLocations = bedManagementDao.getAdmissionLocations(locations);
		
		Assertions.assertEquals(3, admissionLocations.size());
		Assertions.assertEquals("Cardio ward on first floor", admissionLocations.get(0).getWard().getName());
		Assertions.assertFalse(admissionLocations.get(0).getWard().getRetired());
		Assertions.assertEquals("Cardio ward on third floor", admissionLocations.get(1).getWard().getName());
		Assertions.assertFalse(admissionLocations.get(1).getWard().getRetired());
		Assertions.assertEquals("Orthopaedic ward", admissionLocations.get(2).getWard().getName());
		Assertions.assertFalse(admissionLocations.get(2).getWard().getRetired());
	}
	
	@Test
	public void shouldReturnBedLocationMappingByBed() throws Exception {
		Bed bed = bedManagementDao.getBedByUuid("bb02b84b-d225-11e4-9c67-080027b662ab");
		BedLocationMapping bedLocationMapping = bedManagementDao.getBedLocationMappingByBed(bed);
		
		Assertions.assertNotNull(bedLocationMapping);
		Assertions.assertEquals(19, bedLocationMapping.getId());
		Assertions.assertTrue(bedLocationMapping.getBed().getId().equals(11));
	}
	
	@Test
	public void shouldReturnBedLocationMappingByLocationAndRowAndColumn() throws Exception {
		Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
		BedLocationMapping bedLocationMapping = bedManagementDao.getBedLocationMappingByLocationAndRowAndColumn(location, 1,
		    2);
		
		Assertions.assertNotNull(bedLocationMapping);
		Assertions.assertTrue(bedLocationMapping.getLocation().getLocationId().equals(123452));
		Assertions.assertEquals(1, bedLocationMapping.getRow());
		Assertions.assertEquals(2, bedLocationMapping.getColumn());
		
		Location location2 = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
		BedLocationMapping bedLocationMapping2 = bedManagementDao.getBedLocationMappingByLocationAndRowAndColumn(location2,
		    3, 1);
		Assertions.assertNull(bedLocationMapping2);
	}
	
	@Test
	public void shouldReturnBedLocationMappingByLocation() throws Exception {
		Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
		List<BedLocationMapping> bedLocationMappingList = bedManagementDao.getBedLocationMappingsByLocation(location);
		
		Assertions.assertEquals(6, bedLocationMappingList.size());
		Assertions.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56",
		    bedLocationMappingList.get(0).getLocation().getUuid());
		Assertions.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56",
		    bedLocationMappingList.get(5).getLocation().getUuid());
		
		Location location2 = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
		List<BedLocationMapping> bedLocationMappingList2 = bedManagementDao.getBedLocationMappingsByLocation(location2);
		Assertions.assertEquals(18, bedLocationMappingList2.size());
		Assertions.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75",
		    bedLocationMappingList2.get(0).getLocation().getUuid());
		Assertions.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75",
		    bedLocationMappingList2.get(5).getLocation().getUuid());
		Assertions.assertNull(bedLocationMappingList2.get(5).getBed());
	}
	
	@Test
	public void shouldSaveLocationMapping() throws Exception {
		BedLocationMapping bedLocationMapping = new BedLocationMapping();
		bedLocationMapping.setRow(4);
		bedLocationMapping.setColumn(1);
		Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
		bedLocationMapping.setLocation(location);
		bedManagementDao.saveBedLocationMapping(bedLocationMapping);
		
		Assertions.assertNotNull(bedLocationMapping.getId());
		Assertions.assertEquals(4, bedLocationMapping.getRow());
		Assertions.assertEquals(1, bedLocationMapping.getColumn());
		Assertions.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMapping.getLocation().getUuid());
		Assertions.assertNull(bedLocationMapping.getBed());
		Assertions.assertNotNull(bedManagementDao.getBedLocationMappingByLocationAndRowAndColumn(location, 4, 1));
	}
	
	@Test
	public void shouldGetBeds() throws Exception {
		List<Bed> bedList = bedManagementDao.getBeds(null, null, null, null, null);
		
		Assertions.assertEquals(17, bedList.size());
		Assertions.assertEquals("304-a", bedList.get(0).getBedNumber());
		Assertions.assertEquals("304-b", bedList.get(1).getBedNumber());
	}
	
	@Test
	public void shouldGetBedsByLocation() throws Exception {
		Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
		List<Bed> BedList = bedManagementDao.getBeds(location, null, null, null, null);
		
		Assertions.assertEquals(10, BedList.size());
		Assertions.assertEquals("304-a", BedList.get(0).getBedNumber());
		Assertions.assertEquals("304-b", BedList.get(1).getBedNumber());
	}
	
	@Test
	public void shouldGetBedsByBedType() throws Exception {
		BedType bedType = bedManagementDao.getBedTypeById(2);
		List<Bed> bedList = bedManagementDao.getBeds(null, bedType, null, 5, 0);
		
		Assertions.assertEquals(3, bedList.size());
		Assertions.assertEquals("luxury", bedList.get(0).getBedType().getName());
		Assertions.assertEquals("luxury", bedList.get(1).getBedType().getName());
	}
	
	@Test
	public void shouldGetBedsByLocationAndBedType() throws Exception {
		BedType bedType = bedManagementDao.getBedTypeById(2);
		Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
		List<Bed> bedList = bedManagementDao.getBeds(location, bedType, null, 5, 0);
		
		Assertions.assertEquals(2, bedList.size());
		Assertions.assertEquals("bb049d6d-d225-11e4-9c67-080027b662fc", bedList.get(0).getUuid());
		Assertions.assertEquals("luxury", bedList.get(0).getBedType().getName());
		Assertions.assertEquals("luxury", bedList.get(1).getBedType().getName());
	}
	
	@Test
	public void shouldGetBedsByStatus() throws Exception {
		List<Bed> bedList = bedManagementDao.getBeds(null, null, BedStatus.OCCUPIED, 5, 0);
		
		Assertions.assertEquals(3, bedList.size());
		Assertions.assertEquals("OCCUPIED", bedList.get(0).getStatus());
		
		List<Bed> bedList2 = bedManagementDao.getBeds(null, null, BedStatus.AVAILABLE, 5, 0);
		
		Assertions.assertEquals(5, bedList2.size());
		Assertions.assertEquals("AVAILABLE", bedList2.get(0).getStatus());
		Assertions.assertEquals("AVAILABLE", bedList2.get(4).getStatus());
	}
	
	@Test
	public void shouldGetBedsByLocationAndStatus() throws Exception {
		Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
		List<Bed> bedList = bedManagementDao.getBeds(location, null, BedStatus.OCCUPIED, 5, 0);
		
		Assertions.assertEquals(1, bedList.size());
		Assertions.assertEquals("bb02b84b-d225-11e4-9c67-080027b662ec", bedList.get(0).getUuid());
		Assertions.assertEquals("OCCUPIED", bedList.get(0).getStatus());
	}
	
	@Test
	public void shouldGetBedsByBedTypeAndStatus() throws Exception {
		BedType bedType = bedManagementDao.getBedTypeById(3);
		List<Bed> bedList = bedManagementDao.getBeds(null, bedType, BedStatus.AVAILABLE, 20, 0);
		
		Assertions.assertEquals(11, bedList.size());
		Assertions.assertEquals("AVAILABLE", bedList.get(0).getStatus());
		Assertions.assertEquals("normal", bedList.get(0).getBedType().getName());
		Assertions.assertEquals("AVAILABLE", bedList.get(10).getStatus());
		Assertions.assertEquals("normal", bedList.get(10).getBedType().getName());
	}
	
	@Test
	public void shouldGetBedsByLocationAndBedTypeAndStatus() throws Exception {
		BedType bedType = bedManagementDao.getBedTypeById(1);
		Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
		List<Bed> bedList = bedManagementDao.getBeds(location, bedType, BedStatus.AVAILABLE, 10, 0);
		
		Assertions.assertEquals(1, bedList.size());
		Assertions.assertEquals("bb094d57-d225-11e4-9c67-080027b662mh", bedList.get(0).getUuid());
		Assertions.assertEquals("AVAILABLE", bedList.get(0).getStatus());
		Assertions.assertEquals("deluxe", bedList.get(0).getBedType().getName());
	}
	
	@Test
	public void shouldReturnBedListByLocationUuid() throws Exception {
		Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
		List<Bed> bedList = bedManagementDao.getBeds(location, null, null, null, null);
		Assertions.assertEquals(6, bedList.size());
		Assertions.assertEquals("bb02b84b-d225-11e4-9c67-080027b662ab", bedList.get(0).getUuid());
		Assertions.assertFalse(bedList.get(0).getVoided());
	}
	
	@Test
	public void shouldReturnTotalBedNumberByLocationUuid() throws Exception {
		Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
		long num = bedManagementDao.getBedCountByLocation(location);
		
		Assertions.assertEquals(6, num);
	}
	
	@Test
	public void shouldSaveBed() throws Exception {
		Bed bed = new Bed();
		bed.setBedNumber("100-a");
		bed.setStatus("AVAILABLE");
		BedType bedType = bedManagementDao.getBedTypeById(1);
		bed.setBedType(bedType);
		bedManagementDao.saveBed(bed);
		
		Assertions.assertNotNull(bed.getId());
		Assertions.assertNotNull(bedManagementDao.getBedById(bed.getId()));
	}
	
	@Test
	public void shouldGetBedTypeById() throws Exception {
		BedType bedType = bedManagementDao.getBedTypeById(1);
		assertThat(bedType.getName(), is(equalTo("deluxe")));
		
		BedType bedType2 = bedManagementDao.getBedTypeById(2);
		assertThat(bedType2.getName(), is(equalTo("luxury")));
	}
	
	@Test
	public void shouldGetBedTypeByUuid() throws Exception {
		BedType bedType = bedManagementDao.getBedTypeByUuid("6f9fb240-0fd5-11e8-adb7-080027b38971");
		assertThat(bedType.getName(), is(equalTo("luxury")));
		
		BedType bedType2 = bedManagementDao.getBedTypeByUuid("6f9fb341-0fd5-11e8-adb7-080027b38971");
		assertThat(bedType2.getName(), is(equalTo("normal")));
	}
	
	@Test
	public void shouldListBedTypes() throws Exception {
		List<BedType> bedTypeList = bedManagementDao.getBedTypes(null, 3, 0);
		
		Assertions.assertNotNull(bedTypeList);
		Assertions.assertEquals(3, bedTypeList.size());
		Assertions.assertTrue(bedTypeList.get(0).getName().equals("deluxe"));
		Assertions.assertTrue(bedTypeList.get(1).getName().equals("luxury"));
	}
	
	@Test
	public void shouldReturnBedTypeByName() throws Exception {
		BedType bedType = bedManagementDao.getBedTypes("luxury", 1, 0).get(0);
		
		Assertions.assertNotNull(bedType);
		Assertions.assertEquals("luxury", bedType.getName());
	}
	
	@Test
	public void shouldAddNewBedType() throws Exception {
		BedType bedType = new BedType();
		bedType.setName("special");
		bedType.setDisplayName("SPL");
		bedType.setDescription("Special bed");
		bedManagementDao.saveBedType(bedType);
		
		Assertions.assertNotNull(bedType.getId());
		
		BedType specialBedType = bedManagementDao.getBedTypes("special", 1, 0).get(0);
		Assertions.assertNotNull(specialBedType);
		Assertions.assertEquals("special", specialBedType.getName());
	}
	
	@Test
	public void shouldDeleteBedType() throws Exception {
		BedType bedType = new BedType();
		bedType.setName("special");
		bedType.setDisplayName("SPL");
		bedType.setDescription("Special bed");
		bedManagementDao.saveBedType(bedType);
		
		BedType specialBedType = bedManagementDao.getBedTypes("special", 1, 0).get(0);
		bedManagementDao.deleteBedType(specialBedType);
		
		Assertions.assertTrue(bedManagementDao.getBedTypes("special", 1, 0).size() == 0);
	}
	
	@Test
	public void shouldGetBedTagByUuid() throws Exception {
		BedTag bedTag = bedManagementDao.getBedTagByUuid("73e846d6-ed5f-11e6-a3c9-0800274a5156");
		assertThat(bedTag.getName(), is(equalTo("Broken")));
		
		BedTag bedTag2 = bedManagementDao.getBedTagByUuid("73e846d6-ed5f-33e6-a3c9-0800274a5156");
		assertThat(bedTag2.getName(), is(equalTo("Isolation")));
	}
	
	@Test
	public void shouldReturnBedTags() throws Exception {
		List<BedTag> bedTags = bedManagementDao.getBedTags(null, 10, 0);
		
		Assertions.assertEquals(4, bedTags.size());
		Assertions.assertFalse(bedTags.get(0).getVoided());
		Assertions.assertFalse(bedTags.get(1).getVoided());
		Assertions.assertFalse(bedTags.get(2).getVoided());
		Assertions.assertFalse(bedTags.get(3).getVoided());
	}
	
	@Test
	public void shouldReturnBedTagsByName() throws Exception {
		List<BedTag> bedTags = bedManagementDao.getBedTags("Isolation", 10, 0);
		Assertions.assertEquals(1, bedTags.size());
		Assertions.assertFalse(bedTags.get(0).getVoided());
		Assertions.assertEquals("Isolation", bedTags.get(0).getName());
	}
	
	@Test
	public void shouldAddNewBedTag() throws Exception {
		BedTag bedTag = new BedTag();
		bedTag.setName("Reserved");
		bedManagementDao.saveBedTag(bedTag);
		
		Assertions.assertNotNull(bedTag.getId());
		Assertions.assertNotNull(bedTag.getUuid());
	}
	
	@Test
	public void shouldDeleteBedTag() throws Exception {
		BedTag bedTag = bedManagementDao.getBedTagByUuid("73e846d6-ed5f-44e6-a3c9-0800274a5156");
		bedManagementDao.deleteBedTag(bedTag);
		
		bedTag = bedManagementDao.getBedTagByUuid("73e846d6-ed5f-44e6-a3c9-0800274a5156");
		Assertions.assertNull(bedTag);
	}
	
	@Test
	public void shouldDeleteBedLocationMapping() throws Exception {
		Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
		List<BedLocationMapping> beforeDeleteBedLocationMappings = bedManagementDao
		        .getBedLocationMappingsByLocation(location);
		
		bedManagementDao.deleteBedLocationMapping(beforeDeleteBedLocationMappings.get(0));
		List<BedLocationMapping> afterDeleteBedLocationMappings = bedManagementDao
		        .getBedLocationMappingsByLocation(location);
		Assertions.assertEquals(beforeDeleteBedLocationMappings.size() - 1, afterDeleteBedLocationMappings.size());
		Assertions.assertFalse(afterDeleteBedLocationMappings.contains(beforeDeleteBedLocationMappings.get(0)));
	}
	
	@Test
	public void shouldNotGetVoidedBedPatientAssignments() throws Exception {
		String voidedBpaUuid = "00000BAD-BED0-0000-0000-000000000000";
		int voidedBpaBedId = 1;
		
		BedPatientAssignment voidedBpa = bedManagementDao.getBedPatientAssignmentByUuid(voidedBpaUuid);
		Assertions.assertTrue(voidedBpa.getVoided());
		
		Bed bedWithVoidedBpa = bedManagementDao.getBedById(voidedBpaBedId);
		List<BedPatientAssignment> assignmentsByBed = bedManagementDao.getCurrentAssignmentsByBed(bedWithVoidedBpa);
		for (BedPatientAssignment bpa : assignmentsByBed) {
			Assertions.assertNotEquals(voidedBpaUuid, bpa.getUuid());
			Assertions.assertFalse(bpa.getVoided());
		}
		
		Bed bed = bedManagementDao.getLatestBedByVisit("12345678-6b78-11e0-93c3-18a905e044dc");
		Assertions.assertNotEquals(voidedBpaBedId, ((Integer) bed.getId()).intValue());
		
		List<BedPatientAssignment> assignmentsByVisit = bedManagementDao
		        .getBedPatientAssignmentByVisit("12345678-6b78-11e0-93c3-18a905e044dc", true);
		for (BedPatientAssignment bpa : assignmentsByVisit) {
			Assertions.assertNotEquals(voidedBpaUuid, bpa.getUuid());
			Assertions.assertFalse(bpa.getVoided());
		}
		
		List<BedPatientAssignment> assignmentsByPatient = bedManagementDao.getBedPatientAssignmentByPatient("1001", true);
		for (BedPatientAssignment bpa : assignmentsByPatient) {
			Assertions.assertNotEquals(voidedBpaUuid, bpa.getUuid());
			Assertions.assertFalse(bpa.getVoided());
		}
		
		List<BedPatientAssignment> assignmentsByEncounter = bedManagementDao
		        .getBedPatientAssignmentByEncounter("12345678-393b-4118-9c83-a3715b82d4de", true);
		for (BedPatientAssignment bpa : assignmentsByEncounter) {
			Assertions.assertNotEquals(voidedBpaUuid, bpa.getUuid());
			Assertions.assertFalse(bpa.getVoided());
		}
	}
}
