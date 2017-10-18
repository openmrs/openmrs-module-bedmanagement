package org.openmrs.module.bedmanagement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class HibernateBedManagementDAOTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    BedManagementDAO bedManagementDao;

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
    public void shouldReturnAdmissionLocationIds() {
        List<Integer> idList = bedManagementDao.getAdmissionLocationIds();
        Assert.assertEquals(5, idList.size());
        Assert.assertTrue(idList.contains(12345));
        Assert.assertTrue(idList.contains(12346));
    }

    @Test
    public void shouldReturnWards() throws Exception {
        List<Location> wards = bedManagementDao.getWards();
        Assert.assertEquals(3, wards.size());
        Assert.assertEquals("7779d653-393b-4118-9c83-a3715b82d4ac", wards.get(0).getUuid());
        Assert.assertEquals("e26cea2c-1b9f-666e-6511-f3ef6c88af6f", wards.get(1).getUuid());
        Assert.assertEquals("19e023e8-20ee-4237-ade6-9e68f897b7a9", wards.get(2).getUuid());
        Assert.assertFalse(wards.get(2).getRetired());
    }

    @Test
    public void shouldReturnByName() throws Exception {
        List<Location> wards = bedManagementDao.searchWardByName("Orthopaedic ward");
        Assert.assertEquals(1, wards.size());
        Assert.assertEquals("Orthopaedic ward", wards.get(0).getName());
        Assert.assertFalse(wards.get(0).getRetired());
    }

    @Test
    public void shouldReturnWardByUuid() throws Exception {
        Location ward = bedManagementDao.getWardByUuid("19e023e8-20ee-4237-ade6-9e68f897b7a9");

        Assert.assertEquals("Orthopaedic ward", ward.getName());
        Assert.assertEquals("19e023e8-20ee-4237-ade6-9e68f897b7a9", ward.getUuid());
        Assert.assertFalse(ward.getRetired());
    }
}