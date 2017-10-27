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
import org.openmrs.module.bedmanagement.pojo.AdmissionLocation;
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
    public void shouldREturnAdmissionLocationByLocationTagName() throws Exception {
        List<AdmissionLocation> admissionLocations = admissionLocationDao.getAdmissionLocationsByLocationTagName("Admission Location");

        Assert.assertEquals(2, admissionLocations.size());
        Assert.assertEquals("Cardio ward on first floor", admissionLocations.get(0).getWard().getName());
        Assert.assertEquals(10, admissionLocations.get(0).getTotalBeds());
        Assert.assertEquals("Orthopaedic ward", admissionLocations.get(1).getWard().getName());
    }

    @Test
    public void shouldReturnLayoutOfWardRoom() throws Exception {
        Location ward = locationDao.getLocationByUuid("19e023e8-20ee-4237-ade6-9e68f897b7a9");
        AdmissionLocation admissionLocation = admissionLocationDao.getLayoutForWard(ward);

        Assert.assertNotNull(admissionLocation.getBedLayouts());
        Assert.assertEquals(6, admissionLocation.getBedLayouts().size());
        Assert.assertEquals("307-a", admissionLocation.getBedLayouts().get(0).getBedNumber());
        Assert.assertEquals("Physical Location for Orthopaedic ward", admissionLocation.getBedLayouts().get(0).getLocation());
        Assert.assertEquals("306-e", admissionLocation.getBedLayouts().get(5).getBedNumber());
    }

    @Test
    public void shouldReturnWardForBed() throws Exception{
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
    public void shouldReturnWards() throws Exception {
        List<Location> wards = admissionLocationDao.getWards();
        Assert.assertEquals(3, wards.size());
        Assert.assertEquals("7779d653-393b-4118-9c83-a3715b82d4ac", wards.get(0).getUuid());
        Assert.assertEquals("e26cea2c-1b9f-666e-6511-f3ef6c88af6f", wards.get(1).getUuid());
        Assert.assertEquals("19e023e8-20ee-4237-ade6-9e68f897b7a9", wards.get(2).getUuid());
        Assert.assertFalse(wards.get(2).getRetired());
    }

    @Test
    public void shouldReturnWardByUuid() throws Exception {
        Location ward = admissionLocationDao.getWardByLocationUuid("19e023e8-20ee-4237-ade6-9e68f897b7a9");

        Assert.assertEquals("Orthopaedic ward", ward.getName());
        Assert.assertEquals("19e023e8-20ee-4237-ade6-9e68f897b7a9", ward.getUuid());
        Assert.assertFalse(ward.getRetired());
    }

    @Test
    public void shouldReturnByName() throws Exception {
        List<Location> wards = admissionLocationDao.getWardsByName("Orthopaedic ward");
        Assert.assertEquals(1, wards.size());
        Assert.assertEquals("Orthopaedic ward", wards.get(0).getName());
        Assert.assertFalse(wards.get(0).getRetired());
    }
}
