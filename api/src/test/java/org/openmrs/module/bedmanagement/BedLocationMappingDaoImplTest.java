package org.openmrs.module.bedmanagement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.api.db.LocationDAO;
import org.openmrs.module.bedmanagement.dao.BedDao;
import org.openmrs.module.bedmanagement.dao.BedLocationMappingDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BedLocationMappingDaoImplTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    BedLocationMappingDao bedLocationMappingDao;

    @Autowired
    LocationDAO locationDao;

    @Autowired
    BedDao bedDao;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }


    @Test
    public void shouldReturnBedLocationMappingByBed() throws Exception {
        Bed bed = bedDao.getBedByUuid("bb02b84b-d225-11e4-9c67-080027b662ab");
        BedLocationMapping bedLocationMapping = bedLocationMappingDao.getBedLocationMappingByBed(bed);

        Assert.assertNotNull(bedLocationMapping);
        Assert.assertEquals(19, bedLocationMapping.getId());
        Assert.assertTrue(bedLocationMapping.getBed().getId().equals(11));
    }

    @Test
    public void shouldReturnBedLocationMappingByLocationAndRowAndColumn() throws Exception {
        Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
        BedLocationMapping bedLocationMapping = bedLocationMappingDao.getBedLocationMappingByLocationAndRowAndColumn(location, 1, 2);

        Assert.assertNotNull(bedLocationMapping);
        Assert.assertTrue(bedLocationMapping.getLocation().getLocationId().equals(123452));
        Assert.assertEquals(1, bedLocationMapping.getRow());
        Assert.assertEquals(2, bedLocationMapping.getColumn());

        Location location2 = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
        BedLocationMapping bedLocationMapping2 = bedLocationMappingDao.getBedLocationMappingByLocationAndRowAndColumn(location2, 3, 1);
        Assert.assertNull(bedLocationMapping2);
    }

    @Test
    public void shouldReturnBedLocationMappingByLocation() throws Exception {
        Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b56");
        List<BedLocationMapping> bedLocationMappingList = bedLocationMappingDao.getBedLocationMappingByLocation(location);

        Assert.assertEquals(6, bedLocationMappingList.size());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56", bedLocationMappingList.get(0).getLocation().getUuid());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b56", bedLocationMappingList.get(5).getLocation().getUuid());

        Location location2 = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
        List<BedLocationMapping> bedLocationMappingList2 = bedLocationMappingDao.getBedLocationMappingByLocation(location2);
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
        bedLocationMappingDao.saveBedLocationMapping(bedLocationMapping);

        Assert.assertNotNull(bedLocationMapping.getId());
        Assert.assertEquals(4, bedLocationMapping.getRow());
        Assert.assertEquals(1, bedLocationMapping.getColumn());
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMapping.getLocation().getUuid());
        Assert.assertNull(bedLocationMapping.getBed());
        Assert.assertNotNull(bedLocationMappingDao.getBedLocationMappingByLocationAndRowAndColumn(location, 4, 1));
    }

}
