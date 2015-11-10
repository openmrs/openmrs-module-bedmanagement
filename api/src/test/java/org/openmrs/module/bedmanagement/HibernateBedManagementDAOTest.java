package org.openmrs.module.bedmanagement;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class HibernateBedManagementDAOTest extends BaseModuleContextSensitiveTest {

    @Autowired
    BedManagementDAO bedManagementDAO;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }

    @Test
    public void shouldGetBedDetailsByVisit() throws Exception {
        Bed bed = bedManagementDAO.getLatestBedByVisit("8cfda6ae-6b78-11e0-93c3-18a905e044dc");
        assertThat(bed.getId(), is(equalTo(12)));
    }

    @Test
    public void shouldReturnNullIfPatientIsNotAssignedToAnyBed() throws Exception {
        Bed bed = bedManagementDAO.getLatestBedByVisit("7d8c1980-6b78-11e0-93c3-18a905e044dc");
        assertNull(bed);
    }

    @Test
    public void shouldReturnNullIfVisitNotExists() throws Exception {
        Bed bed = bedManagementDAO.getLatestBedByVisit("abcd");
        assertNull(bed);
    }

    @Test
    public void shouldGetTheLatestBedDetailsForAVisit() throws Exception {
        Bed bed = bedManagementDAO.getLatestBedByVisit("e1428fea-6b78-11e0-93c3-18a905e044dc");
        assertThat(bed.getId(), is(equalTo(12)));
    }

    @Test
    public void shouldGetTheLatestBedForSameEncounterAndSameVisit() {
        Bed bed = bedManagementDAO.getLatestBedByVisit("8cfda6ae-6b78-11e0-93c3-18a905e044dc");
        assertThat(bed.getId(), is(equalTo(12)));
    }
}