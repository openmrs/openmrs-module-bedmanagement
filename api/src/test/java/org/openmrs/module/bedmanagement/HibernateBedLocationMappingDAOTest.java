package org.openmrs.module.bedmanagement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.api.db.LocationDAO;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class HibernateBedLocationMappingDAOTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    BedLocationMappingDAO bedLocationMappingDao;

    @Autowired
    LocationDAO locationDao;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }

    @Test
    public void shouldReturnBedListByLocationUuid() throws Exception {
        List<Bed> bedList = bedLocationMappingDao.listBedByLocationUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75", null, null);

        Assert.assertEquals(10, bedList.size());
        Assert.assertEquals("304-a", bedList.get(0).getBedNumber());
    }

    @Test
    public void shouldReturnBedByLocationAndLayout() throws Exception {
        Bed bed = bedLocationMappingDao.getBedByLocationAndLayout("98bc9b32-9d1a-11e2-8137-0800271c1b75", 1, 2);

        Assert.assertNotNull(bed);
        Assert.assertEquals("bb049d6d-d225-11e4-9c67-080027b662ec", bed.getUuid());
        Assert.assertFalse(bed.getVoided());

        Bed bed2 = bedLocationMappingDao.getBedByLocationAndLayout("98bc9b32-9d1a-11e2-8137-0800271c1b75", 2, 1);
        Assert.assertNull(bed2);
    }

    @Test
    public void shouldReturnByLocationAndLayout() throws Exception {
        BedLocationMapping bedLocationMapping = bedLocationMappingDao.getByLocationAndLayout("98bc9b32-9d1a-11e2-8137-0800271c1b75", 1, 2);

        Assert.assertNotNull(bedLocationMapping);
        Assert.assertEquals("98bc9b32-9d1a-11e2-8137-0800271c1b75", bedLocationMapping.getLocation().getUuid());
        Assert.assertEquals(1, bedLocationMapping.getRow());
        Assert.assertEquals(2, bedLocationMapping.getColumn());
    }

    @Test
    public void shouldSaveLocationMapping() throws Exception {
        BedLocationMapping bedLocationMapping = new BedLocationMapping();
        bedLocationMapping.setRow(4);
        bedLocationMapping.setColumn(1);
        Location location = locationDao.getLocationByUuid("98bc9b32-9d1a-11e2-8137-0800271c1b75");
        bedLocationMapping.setLocation(location);
        bedLocationMappingDao.save(bedLocationMapping);

        Assert.assertNotNull(bedLocationMapping.getId());
        Assert.assertNotNull(bedLocationMappingDao.getByLocationAndLayout("98bc9b32-9d1a-11e2-8137-0800271c1b75", 4, 1));
    }

}
