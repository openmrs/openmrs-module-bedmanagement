package org.openmrs.module.bedmanagement;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.dao.BedManagementDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BedManagementDaoImplTest extends BaseModuleContextSensitiveTest {

    private BedManagementDao bedManagementDao;

    @Before
    public void setup() throws Exception {
        executeDataSet("org/openmrs/include/standardTestDataset.xml");
        executeDataSet("bedManagementDAOComponentTestDataset.xml");

        bedManagementDao = Context.getRegisteredComponent("bedManagementDao", BedManagementDao.class);
    }

    @Test
    public void shouldReturnBedByPatient() {
        Patient patient = Context.getPatientService().getPatient(2);

        Bed result = bedManagementDao.getBedByPatient(patient);

        assertEquals("B101", result.getBedNumber());
    }

    @Test
    public void shouldReturnBedPatientAssignmentByUuid() {
        String assignmentUuid = "assignment-uuid-B102";
        BedPatientAssignment result = bedManagementDao.getBedPatientAssignmentByUuid(assignmentUuid);

        Patient patient = Context.getPatientService().getPatient(2);

        assertEquals(patient.getUuid(), result.getPatient().getUuid());
        assertEquals("B102", result.getBed().getBedNumber());
    }

    @Test
    public void shouldReturnCurrentAssignmentsByBed() {
        Bed bed = bedManagementDao.getBedByBedNumber("B103");

        List<BedPatientAssignment> results = bedManagementDao.getCurrentAssignmentsByBed(bed);

        assertEquals(2, results.size());
    }

    @Test
    public void shouldReturnLatestBedByVisit() {
        Patient patient = Context.getPatientService().getPatient(2);

        Visit latestVisit = Context.getVisitService().getVisitsByPatient(patient).stream()
                .max((v1, v2) -> v1.getStartDatetime().compareTo(v2.getStartDatetime()))
                .orElseThrow(() -> new IllegalStateException("No visit found for patient " + patient.getId()));

        Bed latest = bedManagementDao.getLatestBedByVisit(latestVisit.getUuid());

        assertEquals("B105", latest.getBedNumber());
    }
}
