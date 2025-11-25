package org.openmrs.module.bedmanagement;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.dao.BedManagementDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BedManagementDaoImplTest extends BaseModuleContextSensitiveTest {

    private BedManagementDao bedManagementDao;
    private Location defaultLocation;
    private EncounterType defaultEncounterType;

    @Before
    public void setup() throws Exception {
        executeDataSet("org/openmrs/include/standardTestDataset.xml");

        bedManagementDao = Context.getRegisteredComponent("bedManagementDao", BedManagementDao.class);
        assertNotNull("bedManagementDao should be initialized", bedManagementDao);

        defaultLocation = Context.getLocationService()
                .getLocationByUuid("8d6c993e-c2cc-11de-8d13-0010c6dffd0f");
        assertNotNull("Default location should not be null", defaultLocation);

        defaultEncounterType = Context.getEncounterService()
                .getEncounterType("Scheduled");
                
        assertNotNull("Default encounter type should not be null", defaultEncounterType);
    }

    private Bed createBed(String bedNumber) {
        BedType bedType = new BedType();
        bedType.setName("Test BedType");
        bedType.setDisplayName("TB");
        bedType.setDescription("Test Bed Type");
        bedType = bedManagementDao.saveBedType(bedType);

        Bed bed = new Bed();
        bed.setBedNumber(bedNumber);
        bed.setStatus("AVAILABLE");
        bed.setBedType(bedType);

        return bedManagementDao.saveBed(bed);
    }

    private Visit getTestVisit(Patient patient) {
        List<Visit> visits = Context.getVisitService().getVisitsByPatient(patient);
        if (visits.isEmpty()) {
            Visit visit = new Visit();
            visit.setPatient(patient);
            visit.setLocation(defaultLocation);
            visit.setStartDatetime(new Date());
            visit.setVisitType(Context.getVisitService().getAllVisitTypes().get(0));
            return Context.getVisitService().saveVisit(visit);
        }
        return visits.get(0);
    }

    private Encounter getTestEncounter(Patient patient) {
        List<Encounter> encounters = Context.getEncounterService().getEncountersByPatient(patient);

        if (encounters.isEmpty()) {
            Visit visit = getTestVisit(patient);
            Encounter encounter = new Encounter();
            encounter.setPatient(patient);
            encounter.setVisit(visit);
            encounter.setEncounterType(defaultEncounterType);
            encounter.setEncounterDatetime(new Date());
            return Context.getEncounterService().saveEncounter(encounter);
        }

        Encounter encounter = encounters.get(0);

        if (encounter.getEncounterType() == null) {
            encounter.setEncounterType(defaultEncounterType);
            Context.getEncounterService().saveEncounter(encounter);
        }

        return encounter;
    }

    @Test
    public void shouldReturnBedByPatient() {
        Patient patient = Context.getPatientService().getPatient(2);
        Bed bed = createBed("B101");

        Visit visit = getTestVisit(patient);
        Encounter encounter = getTestEncounter(patient);

        BedPatientAssignment assignment = new BedPatientAssignment();
        assignment.setBed(bed);
        assignment.setPatient(patient);
        assignment.setEncounter(encounter);
        assignment.setStartDatetime(new Date());
        assignment.setEndDatetime(null);

        bedManagementDao.saveBedPatientAssignment(assignment);

        Bed result = bedManagementDao.getBedByPatient(patient);

        assertNotNull("Expected a bed to be returned for the patient", result);
        assertEquals("B101", result.getBedNumber());
    }

    @Test
    public void shouldReturnBedPatientAssignmentByUuid() {
        Patient patient = Context.getPatientService().getPatient(6);
        Bed bed = createBed("B102");
        Encounter encounter = getTestEncounter(patient);

        BedPatientAssignment assignment = new BedPatientAssignment();
        assignment.setBed(bed);
        assignment.setPatient(patient);
        assignment.setEncounter(encounter);
        assignment.setStartDatetime(new Date());
        assignment.setEndDatetime(null);

        BedPatientAssignment saved = bedManagementDao.saveBedPatientAssignment(assignment);

        BedPatientAssignment result = bedManagementDao.getBedPatientAssignmentByUuid(saved.getUuid());

        assertNotNull("Expected a BedPatientAssignment to be found by UUID", result);
        assertEquals(patient.getUuid(), result.getPatient().getUuid());
    }

    @Test
    public void shouldReturnCurrentAssignmentsByBed() {
        Bed bed = createBed("B103");
        Patient patient1 = Context.getPatientService().getPatient(7);
        Patient patient2 = Context.getPatientService().getPatient(8);

        Encounter encounter1 = getTestEncounter(patient1);
        Encounter encounter2 = getTestEncounter(patient2);

        BedPatientAssignment assignment1 = new BedPatientAssignment();
        assignment1.setBed(bed);
        assignment1.setPatient(patient1);
        assignment1.setEncounter(encounter1);
        assignment1.setStartDatetime(new Date());
        assignment1.setEndDatetime(null);
        bedManagementDao.saveBedPatientAssignment(assignment1);

        BedPatientAssignment assignment2 = new BedPatientAssignment();
        assignment2.setBed(bed);
        assignment2.setPatient(patient2);
        assignment2.setEncounter(encounter2);
        assignment2.setStartDatetime(new Date());
        assignment2.setEndDatetime(null);
        bedManagementDao.saveBedPatientAssignment(assignment2);

        List<BedPatientAssignment> results =
                bedManagementDao.getCurrentAssignmentsByBed(bed);

        assertNotNull("Expected a list of current assignments", results);
        assertEquals(2, results.size());
    }

    @Test
    public void shouldReturnLatestBedByVisit() {
        Patient patient = Context.getPatientService().getPatient(2);
        Visit visit = getTestVisit(patient);

        Encounter encounter1 = new Encounter();
        encounter1.setPatient(patient);
        encounter1.setVisit(visit);
        encounter1.setEncounterType(defaultEncounterType);
        encounter1.setEncounterDatetime(new Date(System.currentTimeMillis() - 100000L));
        encounter1 = Context.getEncounterService().saveEncounter(encounter1);

        Encounter encounter2 = new Encounter();
        encounter2.setPatient(patient);
        encounter2.setVisit(visit);
        encounter2.setEncounterType(defaultEncounterType);
        encounter2.setEncounterDatetime(new Date());
        encounter2 = Context.getEncounterService().saveEncounter(encounter2);

        Bed bed1 = createBed("B104");
        Bed bed2 = createBed("B105");

        BedPatientAssignment assignment1 = new BedPatientAssignment();
        assignment1.setBed(bed1);
        assignment1.setPatient(patient);
        assignment1.setEncounter(encounter1);
        assignment1.setStartDatetime(new Date(System.currentTimeMillis() - 100000L));
        assignment1.setEndDatetime(new Date(System.currentTimeMillis() - 50000L));
        bedManagementDao.saveBedPatientAssignment(assignment1);

        BedPatientAssignment assignment2 = new BedPatientAssignment();
        assignment2.setBed(bed2);
        assignment2.setPatient(patient);
        assignment2.setEncounter(encounter2);
        assignment2.setStartDatetime(new Date());
        assignment2.setEndDatetime(null);
        bedManagementDao.saveBedPatientAssignment(assignment2);

        Bed latest = bedManagementDao.getLatestBedByVisit(visit.getUuid());

        assertNotNull("Expected the latest bed to be returned for the visit", latest);
        assertEquals("B105", latest.getBedNumber());
    }
}
