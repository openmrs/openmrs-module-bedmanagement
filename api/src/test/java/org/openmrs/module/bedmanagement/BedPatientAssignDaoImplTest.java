package org.openmrs.module.bedmanagement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.module.bedmanagement.dao.BedDao;
import org.openmrs.module.bedmanagement.dao.BedPatientAssignmentDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class BedPatientAssignDaoImplTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    BedPatientAssignmentDao bedPatientAssignmentDao;

    @Autowired
    BedDao bedDao;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }

    @Test
    public void shouldGetBedPatientAssignmentByUuid() throws Exception {
        BedPatientAssignment bedPatientAssignment = bedPatientAssignmentDao.getBedPatientAssignmentByUuid("7819d653-393b-4118-9c83-a3715b82d4dd");

        Assert.assertNotNull(bedPatientAssignment);
        Assert.assertEquals("7819d653-393b-4118-9c83-a3715b82d4dd", bedPatientAssignment.getUuid());
        Assert.assertEquals("bb049d6d-d225-11e4-9c67-080027b662ec", bedPatientAssignment.getBed().getUuid());
        Assert.assertEquals("2b597be0-83c7-4f1d-b3d2-1d61ab128823", bedPatientAssignment.getPatient().getUuid());
        Assert.assertTrue(bedPatientAssignment.getBed().getStatus().equals("OCCUPIED"));
    }

    @Test
    public void shouldGetCurrentAssignmentsByBed() throws Exception {
        Bed bed = bedDao.getBedByUuid("bb049d6d-d225-11e4-9c67-080027b662ec");
        List<BedPatientAssignment> bedPatientAssignments = bedPatientAssignmentDao.getCurrentAssignmentsForBed(bed);

        Assert.assertEquals(1, bedPatientAssignments.size());
        Assert.assertEquals("bb049d6d-d225-11e4-9c67-080027b662ec", bedPatientAssignments.get(0).getBed().getUuid());
        Assert.assertEquals("2b597be0-83c7-4f1d-b3d2-1d61ab128823", bedPatientAssignments.get(0).getPatient().getUuid());


        Bed bed2 = bedDao.getBedByUuid("bb0f8866-d225-11e4-9c67-080027b662ec");
        List<BedPatientAssignment> bedPatientAssignments2 = bedPatientAssignmentDao.getCurrentAssignmentsForBed(bed2);
        Assert.assertEquals(0, bedPatientAssignments2.size());
    }
}
