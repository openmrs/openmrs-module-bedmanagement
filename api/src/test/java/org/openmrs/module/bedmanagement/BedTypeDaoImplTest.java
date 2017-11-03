package org.openmrs.module.bedmanagement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.module.bedmanagement.dao.BedTypeDao;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BedTypeDaoImplTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    BedTypeDao bedTypeDao;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }

    @Test
    public void shouldGetBedTypeById() throws Exception {
        BedType bedType = bedTypeDao.getBedTypeById(1);
        assertThat(bedType.getName(), is(equalTo("deluxe")));

        BedType bedType2 = bedTypeDao.getBedTypeById(2);
        assertThat(bedType2.getName(), is(equalTo("luxury")));
    }

    @Test
    public void shouldListBedTypes() throws Exception {
        List<BedType> bedTypeList = bedTypeDao.getBedTypes(3, 0);

        Assert.assertNotNull(bedTypeList);
        Assert.assertEquals(3, bedTypeList.size());
        Assert.assertTrue(bedTypeList.get(0).getName().equals("deluxe"));
        Assert.assertTrue(bedTypeList.get(1).getName().equals("luxury"));
    }

    @Test
    public void shouldReturnBedTypeByName() throws Exception {
        BedType bedType = bedTypeDao.getBedTypesByName("luxury", 1, 0).get(0);

        Assert.assertNotNull(bedType);
        Assert.assertEquals("luxury", bedType.getName());
    }

    @Test
    public void shouldAddNewBedType() throws Exception {
        BedType bedType = new BedType();
        bedType.setName("special");
        bedType.setDisplayName("SPL");
        bedType.setDescription("Special bed");
        bedTypeDao.saveBedType(bedType);

        Assert.assertNotNull(bedType.getId());

        BedType specialBedType = bedTypeDao.getBedTypesByName("special", 1, 0).get(0);
        Assert.assertNotNull(specialBedType);
    }

    @Test
    public void shouldDeleteBedType() throws Exception {
        BedType bedType = new BedType();
        bedType.setName("special");
        bedType.setDisplayName("SPL");
        bedType.setDescription("Special bed");
        bedTypeDao.saveBedType(bedType);

        BedType specialBedType = bedTypeDao.getBedTypesByName("special", 1, 0).get(0);
        bedTypeDao.deleteBedType(specialBedType);

        Assert.assertTrue(bedTypeDao.getBedTypesByName("special", 1, 0).size() == 0);
    }
}
