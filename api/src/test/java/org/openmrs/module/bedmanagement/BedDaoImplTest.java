package org.openmrs.module.bedmanagement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.api.db.LocationDAO;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.dao.BedDao;
import org.openmrs.module.bedmanagement.dao.BedTypeDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BedDaoImplTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    BedDao bedDao;

    @Autowired
    BedTypeDao bedTypeDao;

    @Autowired
    LocationDAO locationDao;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }

    @Test
    public void shouldReturnById() throws Exception {
        Bed bed = bedDao.getBedById(1);

        Assert.assertEquals("304-a", bed.getBedNumber());
        Assert.assertFalse(bed.getVoided());
    }

    @Test
    public void shouldReturnByUuid() throws Exception {
        Bed bed = bedDao.getBedByUuid("bb049d6d-d225-11e4-9c67-080027b662ec");

        Assert.assertEquals("307-b", bed.getBedNumber());
        Assert.assertFalse(bed.getVoided());
    }

    @Test
    public void shouldGetBeds() throws Exception {
        List<Bed> bedList = bedDao.getBeds(null, null);

        Assert.assertEquals(17, bedList.size());
        Assert.assertEquals("304-a", bedList.get(0).getBedNumber());
        Assert.assertEquals("304-b", bedList.get(1).getBedNumber());

        List<Bed> bedList2 = bedDao.getBeds(10, 0);
        Assert.assertEquals(10, bedList2.size());
    }

    @Test
    public void shouldGetBedsByLocation() throws Exception {
        Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
        List<Bed> allBedListOfLocation = bedDao.getBedsByLocation(location, null, null);

        Assert.assertEquals(10, allBedListOfLocation.size());

        List<Bed> BedListOfLocationWithLimit = bedDao.getBedsByLocation(location, 3, 0);
        Assert.assertEquals(3, BedListOfLocationWithLimit.size());
        Assert.assertEquals("304-a", BedListOfLocationWithLimit.get(0).getBedNumber());
        Assert.assertEquals("304-b", BedListOfLocationWithLimit.get(1).getBedNumber());
    }

    @Test
    public void shouldGetBedsByBedType() throws Exception {
        BedType bedType = bedTypeDao.getBedTypeById(2);
        List<Bed> bedList = bedDao.getBedsByBedType(bedType, 5, 0);

        Assert.assertEquals(3, bedList.size());
        Assert.assertEquals("luxury", bedList.get(0).getBedType().getName());
        Assert.assertEquals("luxury", bedList.get(1).getBedType().getName());
    }

    @Test
    public void shouldGetBedsByLocationAndBedType() throws Exception {
        BedType bedType = bedTypeDao.getBedTypeById(2);
        Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
        List<Bed> bedList = bedDao.getBedsByLocationAndBedType(location, bedType, 5, 0);

        Assert.assertEquals(2, bedList.size());
        Assert.assertEquals("bb049d6d-d225-11e4-9c67-080027b662fc", bedList.get(0).getUuid());
        Assert.assertEquals("luxury", bedList.get(0).getBedType().getName());
        Assert.assertEquals("luxury", bedList.get(1).getBedType().getName());
    }

    @Test
    public void shouldGetBedsByStatus() throws Exception {
        List<Bed> bedList = bedDao.getBedsByStatus(BedStatus.OCCUPIED, 5, 0);

        Assert.assertEquals(3, bedList.size());
        Assert.assertEquals("OCCUPIED", bedList.get(0).getStatus());

        List<Bed> bedList2 = bedDao.getBedsByStatus(BedStatus.AVAILABLE, 5, 0);

        Assert.assertEquals(5, bedList2.size());
        Assert.assertEquals("AVAILABLE", bedList2.get(0).getStatus());
        Assert.assertEquals("AVAILABLE", bedList2.get(4).getStatus());
    }

    @Test
    public void shouldGetBedsByLocationAndStatus() throws Exception {
        Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
        List<Bed> bedList = bedDao.getBedsByLocationAndStatus(location, BedStatus.OCCUPIED, 5, 0);

        Assert.assertEquals(1, bedList.size());
        Assert.assertEquals("bb02b84b-d225-11e4-9c67-080027b662ec", bedList.get(0).getUuid());
        Assert.assertEquals("OCCUPIED", bedList.get(0).getStatus());
    }

    @Test
    public void shouldGetBedsByBedTypeAndStatus() throws Exception {
        BedType bedType = bedTypeDao.getBedTypeById(3);
        List<Bed> bedList = bedDao.getBedsByBedTypeAndStatus(bedType, BedStatus.AVAILABLE, 20, 0);

        Assert.assertEquals(11, bedList.size());
        Assert.assertEquals("AVAILABLE", bedList.get(0).getStatus());
        Assert.assertEquals("normal", bedList.get(0).getBedType().getName());
        Assert.assertEquals("AVAILABLE", bedList.get(10).getStatus());
        Assert.assertEquals("normal", bedList.get(10).getBedType().getName());
    }

    @Test
    public void shouldGetBedsByLocationAndBedTypeAndStatus() throws Exception {
        BedType bedType = bedTypeDao.getBedTypeById(1);
        Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
        List<Bed> bedList = bedDao.getBedsByLocationAndBedTypeAndStatus(location, bedType, BedStatus.AVAILABLE, 10, 0);

        Assert.assertEquals(1, bedList.size());
        Assert.assertEquals("bb094d57-d225-11e4-9c67-080027b662mh", bedList.get(0).getUuid());
        Assert.assertEquals("AVAILABLE", bedList.get(0).getStatus());
        Assert.assertEquals("deluxe", bedList.get(0).getBedType().getName());
    }

    @Test
    public void shouldReturnBedListByLocationUuid() throws Exception {
        List<Bed> bedList = bedDao.getBedsByLocationUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
        Assert.assertEquals(6, bedList.size());
        Assert.assertEquals("bb02b84b-d225-11e4-9c67-080027b662ab", bedList.get(0).getUuid());
        Assert.assertFalse(bedList.get(0).getVoided());
    }

    @Test
    public void shouldReturnTotalBedNumberByLocationUuid() throws Exception {
        Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
        long num = bedDao.getBedCountByLocation(location);

        Assert.assertEquals(6l, num);
    }

    @Test
    public void shouldSaveBed() throws Exception {
        Bed bed = new Bed();
        bed.setBedNumber("100-a");
        bed.setStatus("AVAILABLE");
        BedType bedType = bedTypeDao.getBedTypeById(1);
        bed.setBedType(bedType);
        bedDao.saveBed(bed);

        Assert.assertNotNull(bed.getId());
        Assert.assertNotNull(bedDao.getBedById(bed.getId()));
    }

    @Test
    public void shouldReturnBedByLocationAndRowColumn() throws Exception {
        Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
        Bed bed = bedDao.getBedByLocationAndRowColumn(location, 1, 2);

        Assert.assertNotNull(bed);
    }

    @Test
    public void shouldGetLatestBedByVisitUuid() throws Exception {
        Bed bed = bedDao.getLatestBedByVisitUuid("8cfda6ae-6b78-11e0-93c3-18a905e044dc");

        Assert.assertNotNull(bed);
        Assert.assertThat(bed.getId(), is(equalTo(12)));

        Bed bed2 = bedDao.getLatestBedByVisitUuid("acddd");

        Assert.assertNull(bed2);
    }

}
