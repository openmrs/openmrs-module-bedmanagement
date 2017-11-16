package org.openmrs.module.bedmanagement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.db.LocationDAO;
import org.openmrs.module.bedmanagement.constants.BedManagementApiConstants;
import org.openmrs.module.bedmanagement.dao.BedManagementDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class HibernateBedManagementDaoTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    BedManagementDao bedManagementDao;

    @Autowired
    LocationDAO locationDao;

    @Before
    public void beforeAllTests() throws Exception {
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

        Assert.assertNotNull(ward);
        Assert.assertEquals("Physical Location for Orthopaedic ward", ward.getName());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56", ward.getUuid());
    }

    @Test
    public void shouldReturnAdmissionLocationLayoutByLocation() throws Exception {
        Location location = locationDao.getLocationByUuid("19e023e8-20ee-4237-ade6-9e68f897b7a9");
        List<BedLayout> bedLayouts = bedManagementDao.getBedLayoutByLocation(location);

        Assert.assertEquals(6, bedLayouts.size());
        Assert.assertEquals("307-a", bedLayouts.get(0).getBedNumber());
        Assert.assertEquals("OCCUPIED", bedLayouts.get(0).getStatus());
        Assert.assertEquals("Physical Location for Orthopaedic ward", bedLayouts.get(0).getLocation());
        Assert.assertEquals("306-e", bedLayouts.get(5).getBedNumber());
        Assert.assertEquals("AVAILABLE", bedLayouts.get(5).getStatus());
        Assert.assertEquals("Physical Location for Orthopaedic ward", bedLayouts.get(5).getLocation());

        Location location2 = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
        List<BedLayout> locationBedLayouts = bedManagementDao.getBedLayoutByLocation(location2);
        Assert.assertEquals(18, locationBedLayouts.size());
        Assert.assertEquals("304-a", locationBedLayouts.get(0).getBedNumber());
        Assert.assertEquals("OCCUPIED", locationBedLayouts.get(0).getStatus());
        Assert.assertEquals("Physical Location for Cardio ward on first floor", locationBedLayouts.get(0).getLocation());
        Assert.assertNull(locationBedLayouts.get(4).getBedId());
        Assert.assertEquals(2, locationBedLayouts.get(4).getRowNumber().intValue());
        Assert.assertEquals(1, locationBedLayouts.get(4).getColumnNumber().intValue());
    }

    @Test
    public void shouldReturnAdmissionLocationByLocation() throws Exception {
        Location location = locationDao.getLocationByUuid("19e023e8-20ee-4237-ade6-9e68f897b7a9");
        AdmissionLocation admissionLocation = bedManagementDao.getAdmissionLocationForLocation(location);
        List<BedLayout> bedLayouts = admissionLocation.getBedLayouts();

        Assert.assertEquals(6, admissionLocation.getTotalBeds());
        Assert.assertEquals(6, admissionLocation.getBedLayouts().size());
        Assert.assertEquals(2, admissionLocation.getOccupiedBeds());
        Assert.assertEquals("19e023e8-20ee-4237-ade6-9e68f897b7a9", admissionLocation.getWard().getUuid());
        Assert.assertEquals("307-a", bedLayouts.get(0).getBedNumber());
        Assert.assertEquals("OCCUPIED", bedLayouts.get(0).getStatus());
        Assert.assertEquals("Physical Location for Orthopaedic ward", bedLayouts.get(0).getLocation());
        Assert.assertEquals("306-e", bedLayouts.get(5).getBedNumber());
        Assert.assertEquals("AVAILABLE", bedLayouts.get(5).getStatus());
        Assert.assertEquals("Physical Location for Orthopaedic ward", bedLayouts.get(5).getLocation());
    }

    @Test
    public void shouldReturnAllAdmissionLocations() throws Exception {
        LocationTag admissionLocationTag = locationDao.getLocationTagByName(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION);
        List<Location> locations = new ArrayList<>();
        for (Location l : locationDao.getAllLocations(false)) {
            if (l.getTags().contains(admissionLocationTag)) {
                locations.add(l);
            }
        }

        List<AdmissionLocation> admissionLocations = bedManagementDao.getAdmissionLocations(locations);

        Assert.assertEquals(3, admissionLocations.size());
        Assert.assertEquals("Cardio ward on first floor", admissionLocations.get(0).getWard().getName());
        Assert.assertFalse(admissionLocations.get(0).getWard().getRetired());
        Assert.assertEquals("Cardio ward on third floor", admissionLocations.get(1).getWard().getName());
        Assert.assertFalse(admissionLocations.get(1).getWard().getRetired());
        Assert.assertEquals("Orthopaedic ward", admissionLocations.get(2).getWard().getName());
        Assert.assertFalse(admissionLocations.get(2).getWard().getRetired());
    }

    @Test
    public void shouldReturnBedLocationMappingByBed() throws Exception {
        Bed bed = bedManagementDao.getBedByUuid("bb02b84b-d225-11e4-9c67-080027b662ab");
        BedLocationMapping bedLocationMapping = bedManagementDao.getBedLocationMappingByBed(bed);

        Assert.assertNotNull(bedLocationMapping);
        Assert.assertEquals(19, bedLocationMapping.getId());
        Assert.assertTrue(bedLocationMapping.getBed().getId().equals(11));
    }

    @Test
    public void shouldReturnBedLocationMappingByLocationAndRowAndColumn() throws Exception {
        Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
        BedLocationMapping bedLocationMapping = bedManagementDao.getBedLocationMappingByLocationAndRowAndColumn(location, 1, 2);

        Assert.assertNotNull(bedLocationMapping);
        Assert.assertTrue(bedLocationMapping.getLocation().getLocationId().equals(123452));
        Assert.assertEquals(1, bedLocationMapping.getRow());
        Assert.assertEquals(2, bedLocationMapping.getColumn());

        Location location2 = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
        BedLocationMapping bedLocationMapping2 = bedManagementDao.getBedLocationMappingByLocationAndRowAndColumn(location2, 3, 1);
        Assert.assertNull(bedLocationMapping2);
    }

    @Test
    public void shouldReturnBedLocationMappingByLocation() throws Exception {
        Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
        List<BedLocationMapping> bedLocationMappingList = bedManagementDao.getBedLocationMappingByLocation(location);

        Assert.assertEquals(6, bedLocationMappingList.size());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56", bedLocationMappingList.get(0).getLocation().getUuid());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56", bedLocationMappingList.get(5).getLocation().getUuid());

        Location location2 = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
        List<BedLocationMapping> bedLocationMappingList2 = bedManagementDao.getBedLocationMappingByLocation(location2);
        Assert.assertEquals(18, bedLocationMappingList2.size());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMappingList2.get(0).getLocation().getUuid());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMappingList2.get(5).getLocation().getUuid());
        Assert.assertNull(bedLocationMappingList2.get(5).getBed());
    }

    @Test
    public void shouldSaveLocationMapping() throws Exception {
        BedLocationMapping bedLocationMapping = new BedLocationMapping();
        bedLocationMapping.setRow(4);
        bedLocationMapping.setColumn(1);
        Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
        bedLocationMapping.setLocation(location);
        bedManagementDao.saveBedLocationMapping(bedLocationMapping);

        Assert.assertNotNull(bedLocationMapping.getId());
        Assert.assertEquals(4, bedLocationMapping.getRow());
        Assert.assertEquals(1, bedLocationMapping.getColumn());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMapping.getLocation().getUuid());
        Assert.assertNull(bedLocationMapping.getBed());
        Assert.assertNotNull(bedManagementDao.getBedLocationMappingByLocationAndRowAndColumn(location, 4, 1));
    }
}