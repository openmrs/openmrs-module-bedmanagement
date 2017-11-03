package org.openmrs.module.bedmanagement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.api.db.LocationDAO;
import org.openmrs.module.bedmanagement.dao.AdmissionLocationDao;
import org.openmrs.module.bedmanagement.dao.BedDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class AdmissionLocationDaoImplTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    AdmissionLocationDao admissionLocationDao;

    @Autowired
    LocationDAO locationDao;

    @Autowired
    BedDao bedDao;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }

    @Test
    public void shouldReturnWardForBed() throws Exception {
        Bed bed = bedDao.getBedByUuid("bb0906fa-d225-11e4-9c67-080027b662ec");
        Location ward = admissionLocationDao.getWardForBed(bed);

        Assert.assertNotNull(ward);
        Assert.assertEquals("Physical Location for Orthopaedic ward", ward.getName());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56", ward.getUuid());
    }

    @Test
    public void shouldReturnAdmissionLocationIds() {
        List<Integer> idList = admissionLocationDao.getAdmissionLocationIds();
        Assert.assertEquals(5, idList.size());
        Assert.assertTrue(idList.contains(12345));
        Assert.assertTrue(idList.contains(12346));
    }

    @Test
    public void shouldReturnAdmissionLocationLayoutByLocation() throws Exception {
        Location location = locationDao.getLocationByUuid("19e023e8-20ee-4237-ade6-9e68f897b7a9");
        List<BedLayout> bedLayouts = admissionLocationDao.getBedLayoutByLocation(location);

        Assert.assertEquals(6, bedLayouts.size());
        Assert.assertEquals("307-a", bedLayouts.get(0).getBedNumber());
        Assert.assertEquals("OCCUPIED", bedLayouts.get(0).getStatus());
        Assert.assertEquals("Physical Location for Orthopaedic ward", bedLayouts.get(0).getLocation());
        Assert.assertEquals("306-e", bedLayouts.get(5).getBedNumber());
        Assert.assertEquals("AVAILABLE", bedLayouts.get(5).getStatus());
        Assert.assertEquals("Physical Location for Orthopaedic ward", bedLayouts.get(5).getLocation());

        Location location2 = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
        List<BedLayout> locationBedLayouts = admissionLocationDao.getBedLayoutByLocation(location2);
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
        AdmissionLocation admissionLocation = admissionLocationDao.getAdmissionLocationsByLocation(location);
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
        List<AdmissionLocation> admissionLocations = admissionLocationDao.getAdmissionLocations();

        Assert.assertEquals(3, admissionLocations.size());
        Assert.assertEquals("Cardio ward on first floor", admissionLocations.get(0).getWard().getName());
        Assert.assertFalse(admissionLocations.get(0).getWard().getRetired());
        Assert.assertEquals("Cardio ward on third floor", admissionLocations.get(1).getWard().getName());
        Assert.assertFalse(admissionLocations.get(1).getWard().getRetired());
        Assert.assertEquals("Orthopaedic ward", admissionLocations.get(2).getWard().getName());
        Assert.assertFalse(admissionLocations.get(2).getWard().getRetired());
    }
}
