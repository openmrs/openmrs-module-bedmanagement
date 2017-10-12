package org.openmrs.module.bedmanagement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class HiberanateBedDAOTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    BedDAO bedDao;

    @Autowired
    BedTypeDAO bedTypeDao;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }

    @Test
    public void shouldReturnById() throws Exception {
        Bed bed = bedDao.getById(1);

        Assert.assertEquals("304-a", bed.getBedNumber());
        Assert.assertFalse(bed.getVoided());
    }

    @Test
    public void shouldReturnByUuid() throws Exception {
        Bed bed = bedDao.getByUuid("bb049d6d-d225-11e4-9c67-080027b662ec");

        Assert.assertEquals("307-b", bed.getBedNumber());
        Assert.assertFalse(bed.getVoided());
    }

    @Test
    public void shouldListAllBeds() throws Exception {
        List<Bed> bedList = bedDao.getAll(null, 3, 0);

        Assert.assertEquals(3, bedList.size());
        Assert.assertEquals("304-a", bedList.get(0).getBedNumber());
        Assert.assertEquals("304-b", bedList.get(1).getBedNumber());
    }

    @Test
    public void shouldSearchByBedType() throws Exception {
        List<Bed> bedList = bedDao.searchByBedType(null, "deluxe", null, null);

        Assert.assertEquals(3, bedList.size());
        Assert.assertEquals("deluxe", bedList.get(0).getBedType().getName());
        Assert.assertEquals("deluxe", bedList.get(1).getBedType().getName());

        List<Bed> normalBedList = bedDao.searchByBedType(null, "normal", 3, 0);
        Assert.assertEquals(3, normalBedList.size());
        Assert.assertEquals("normal", normalBedList.get(0).getBedType().getName());
        Assert.assertEquals("normal", normalBedList.get(2).getBedType().getName());
    }

    @Test
    public void shouldSearchByStatus() throws Exception {
        List<Bed> bedList = bedDao.searchByBedStatus(null, "OCCUPIED", 5, 0);

        Assert.assertEquals(3, bedList.size());
        Assert.assertEquals("OCCUPIED", bedList.get(0).getStatus());
    }

    @Test
    public void shouldSearchByStatusAndLocationUuid() throws Exception {
        List<Bed> bedList = bedDao.searchByBedStatus("98bc9b32-9d1a-11e2-8137-0800271c1b75", "OCCUPIED", 5, 0);

        Assert.assertEquals(1, bedList.size());
        Assert.assertEquals("OCCUPIED", bedList.get(0).getStatus());
    }

    @Test
    public void shouldSearchByBedTypeAndStatus() throws Exception {
        List<Bed> bedList = bedDao.searchByBedTypeAndStatus(null, "deluxe", "AVAILABLE", 5, 0);
        Assert.assertEquals(1, bedList.size());
        Assert.assertEquals("AVAILABLE", bedList.get(0).getStatus());
        Assert.assertEquals("deluxe", bedList.get(0).getBedType().getName());
    }

    @Test
    public void shouldSaveBed() throws Exception {
        Bed bed = new Bed();
        bed.setBedNumber("100-a");
        bed.setStatus("AVAILABLE");
        BedType bedType = bedTypeDao.getById(1);
        bed.setBedType(bedType);
        bedDao.save(bed);

        Assert.assertNotNull(bed.getId());
        Assert.assertNotNull(bedDao.getById(bed.getId()));
    }

    @Test
    public void shouldReturnBedListByLocationUuid() throws Exception {
        List<Bed> bedList = bedDao.getByLocationUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
        Assert.assertEquals(6, bedList.size());
        Assert.assertEquals("bb02b84b-d225-11e4-9c67-080027b662ab", bedList.get(0).getUuid());
        Assert.assertFalse(bedList.get(0).getVoided());
    }

    @Test
    public void shouldReturnBedNumber() throws Exception {
        long num = bedDao.getTotalBedByLocationUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
        System.out.print(num);
        Assert.assertEquals(6l, num);
    }

    @Test
    public void shouldReturnBedLocationMappingByBedId() throws Exception{
        BedLocationMapping bedLocationMapping = bedDao.getBedLocationMappingByBedId(11);

        Assert.assertNotNull(bedLocationMapping);
        Assert.assertEquals(19, bedLocationMapping.getId());
        Assert.assertTrue(bedLocationMapping.getBed().getId().equals(11));
    }

    @Test
    public void shouldReturnBedLocationMappingByLocationUuidAndPosition() throws Exception {
        BedLocationMapping bedLocationMapping = bedDao.getBedLocationMappingByLocationAndLayout("98bc9b32-9d1a-11e2-8137-0800271c1b56", 1, 2);

        Assert.assertNotNull(bedLocationMapping);
        Assert.assertTrue(bedLocationMapping.getLocation().getLocationId().equals(123452));
        Assert.assertEquals(1, bedLocationMapping.getRow());
        Assert.assertEquals(2, bedLocationMapping.getColumn());

        BedLocationMapping bedLocationMapping2 = bedDao.getBedLocationMappingByLocationAndLayout("98bc9b32-9d1a-11e2-8137-0800271c1b56", 3, 1);
        Assert.assertNull(bedLocationMapping2);
    }

}
