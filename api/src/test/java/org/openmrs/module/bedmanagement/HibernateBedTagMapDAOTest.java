package org.openmrs.module.bedmanagement;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class HibernateBedTagMapDAOTest extends BaseModuleWebContextSensitiveTest {
    @Autowired
    BedTagMapDAO bedTagMapDAO;

    @Autowired
    BedManagementDAO bedManagementDAO;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("bedTagMapTestDataSet.xml");
    }

    @Test
    public void shouldGetBedTagMapByUuid() throws Exception {
        BedTagMap bedTagMap = bedTagMapDAO.getBedTagMapByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f4");
        Bed bed = bedManagementDAO.getBedByUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb");
        BedTag bedTag = bedTagMapDAO.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
        assertNotNull(bedTagMap);
        assertEquals(bed, bedTagMap.getBed());
        assertEquals(bedTag, bedTagMap.getBedTag());
    }

    @Test
    public void shouldReturnNullIfNoBedTagMapForGivenUuid() throws Exception {
        BedTagMap bedTagMap = bedTagMapDAO.getBedTagMapByUuid("wrong uuid");
        assertNull(bedTagMap);
    }

    @Test
    public void shouldGetBedTagByUuid() throws Exception {
        BedTag bedTag = bedTagMapDAO.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
        assertNotNull(bedTag);
        assertEquals("Oxygen", bedTag.getName());
    }

    @Test
    public void shouldReturnNullIfNoBedTagForGivenUuid() throws Exception {
        BedTag bedTag = bedTagMapDAO.getBedTagByUuid("wrong uuid");
        assertNull(bedTag);
    }

    @Test
    public void shouldGetBedTagMapForAGivenBedAndBedTag() throws Exception {
        Bed bed = bedManagementDAO.getBedByUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb");
        BedTag bedTag = bedTagMapDAO.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f1");
        BedTagMap actualBedTagMap = bedTagMapDAO.getBedTagMapWithBedAndTag(bed, bedTag);
        BedTagMap expectedBedTagMap = bedTagMapDAO.getBedTagMapByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f4");
        assertNotNull(actualBedTagMap);
        assertEquals(expectedBedTagMap, actualBedTagMap);
    }

    @Test
    public void shouldReturnNullWhenGivenBedAndBedTagAreNotMapped() throws Exception {
        Bed bed = bedManagementDAO.getBedByUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb");
        BedTag bedTag = bedTagMapDAO.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f2");
        BedTagMap actualBedTagMap = bedTagMapDAO.getBedTagMapWithBedAndTag(bed, bedTag);
        assertNull(actualBedTagMap);
    }

    @Test
    public void shouldAssignBedWithATag() throws Exception {
        Bed bed = bedManagementDAO.getBedByUuid("5580cddd-c290-66c8-8d3a-96dc33d199fb");
        BedTag bedTag = bedTagMapDAO.getBedTagByUuid("5580cddd-c290-66c8-8d3a-96dc33d199f2");
        BedTagMap bedTagMap = new BedTagMap();
        bedTagMap.setBed(bed);
        bedTagMap.setBedTag(bedTag);
        bedTagMapDAO.saveOrUpdate(bedTagMap);
        BedTagMap savedBedTagMap = bedTagMapDAO.getBedTagMapWithBedAndTag(bed, bedTag);
        assertEquals(bed, savedBedTagMap.getBed());
        assertEquals(bedTag, savedBedTagMap.getBedTag());
    }
}