package org.openmrs.module.bedmanagement;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.module.bedmanagement.dao.BedManagementDao;
import org.openmrs.module.bedmanagement.dao.BedTagMapDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.entity.BedTagMap;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class HibernateBedTagMapDaoTest extends BaseModuleWebContextSensitiveTest {
    @Autowired
    BedTagMapDao bedTagMapDao;

    @Autowired
    BedManagementDao bedManagementDao;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("bedTagMapTestDataSet.xml");
    }

    @Test
    public void shouldGetBedTagMapByUuid() throws Exception {
        BedTagMap bedTagMap = bedTagMapDao.getBedTagMapByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f4");
        Bed bed = bedManagementDao.getBedByUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb");
        BedTag bedTag = bedTagMapDao.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
        assertNotNull(bedTagMap);
        assertEquals(bed, bedTagMap.getBed());
        assertEquals(bedTag, bedTagMap.getBedTag());
    }

    @Test
    public void shouldReturnNullIfNoBedTagMapForGivenUuid() throws Exception {
        BedTagMap bedTagMap = bedTagMapDao.getBedTagMapByUuid("wrong uuid");
        assertNull(bedTagMap);
    }

    @Test
    public void shouldGetBedTagByUuid() throws Exception {
        BedTag bedTag = bedTagMapDao.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
        assertNotNull(bedTag);
        assertEquals("Oxygen", bedTag.getName());
    }

    @Test
    public void shouldReturnNullIfNoBedTagForGivenUuid() throws Exception {
        BedTag bedTag = bedTagMapDao.getBedTagByUuid("wrong uuid");
        assertNull(bedTag);
    }

    @Test
    public void shouldGetBedTagMapForAGivenBedAndBedTag() throws Exception {
        Bed bed = bedManagementDao.getBedByUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb");
        BedTag bedTag = bedTagMapDao.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
        BedTagMap actualBedTagMap = bedTagMapDao.getBedTagMapWithBedAndTag(bed, bedTag);
        BedTagMap expectedBedTagMap = bedTagMapDao.getBedTagMapByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f4");
        assertNotNull(actualBedTagMap);
        assertEquals(expectedBedTagMap, actualBedTagMap);
    }

    @Test
    public void shouldReturnNullWhenGivenBedAndBedTagAreNotMapped() throws Exception {
        Bed bed = bedManagementDao.getBedByUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb");
        BedTag bedTag = bedTagMapDao.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f2");
        BedTagMap actualBedTagMap = bedTagMapDao.getBedTagMapWithBedAndTag(bed, bedTag);
        assertNull(actualBedTagMap);
    }

    @Test
    public void shouldAssignBedWithATag() throws Exception {
        Bed bed = bedManagementDao.getBedByUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb");
        BedTag bedTag = bedTagMapDao.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f2");
        BedTagMap bedTagMap = new BedTagMap();
        bedTagMap.setBed(bed);
        bedTagMap.setBedTag(bedTag);
        bedTagMapDao.saveOrUpdate(bedTagMap);
        BedTagMap savedBedTagMap = bedTagMapDao.getBedTagMapWithBedAndTag(bed, bedTag);
        assertEquals(bed, savedBedTagMap.getBed());
        assertEquals(bedTag, savedBedTagMap.getBedTag());
    }
}