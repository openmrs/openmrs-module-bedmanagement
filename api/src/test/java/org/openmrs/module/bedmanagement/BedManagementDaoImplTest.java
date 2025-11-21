package org.openmrs.module.bedmanagement;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.dao.BedManagementDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BedManagementDaoImplTest extends BaseModuleContextSensitiveTest {

	private BedManagementDao bedManagementDao;

	private Location defaultLocation;

	private EncounterType defaultEncounterType;

	@Before
	public void setup() {
		bedManagementDao = Context.getRegisteredComponent("bedManagementDao", BedManagementDao.class);
		assertNotNull("bedManagementDao should be initialized", bedManagementDao);

		defaultLocation = Context.getLocationService().getLocationByUuid("8d6c993e-c2cc-11de-8d13-0010c6dffd0f");
		assertNotNull("Default location should not be null", defaultLocation);

		defaultEncounterType = getOrCreateEncounterType("Test Encounter Type");
	}

	private EncounterType getOrCreateEncounterType(String name) {
		EncounterType encounterType = Context.getEncounterService().getEncounterType(name);

		if (encounterType != null) {
			return encounterType;
		}

		EncounterType newType = new EncounterType();
		newType.setName(name);
		newType.setDescription("Created for testing: " + name);
		Context.getEncounterService().saveEncounterType(newType);
		return newType;
	}

	private Patient createPatient(String identifierString, String firstName, String middleName, String lastName) {
		PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierTypeByName("Test Identifier Type");
		if (pit == null) {
			pit = new PatientIdentifierType();
			pit.setName("Test Identifier Type");
			pit.setDescription("Test Identifier Type Description");
			Context.getPatientService().savePatientIdentifierType(pit);
		}

		PatientIdentifier identifier = new PatientIdentifier(identifierString, pit, defaultLocation);
		identifier.setPreferred(true);

		PersonName name = new PersonName(firstName, middleName, lastName);
		name.setPreferred(true);

		Patient patient = new Patient();
		patient.addIdentifier(identifier);
		patient.addName(name);
		patient.setGender("M");

		return Context.getPatientService().savePatient(patient);
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

	private VisitType getOrCreateVisitType(String name) {
		for (VisitType vt : Context.getVisitService().getAllVisitTypes()) {
			if (name.equals(vt.getName())) {
				return vt;
			}
		}
		VisitType newType = new VisitType();
		newType.setName(name);
		newType.setDescription("Test Visit Type");
		return Context.getVisitService().saveVisitType(newType);
	}

	private Visit createVisit(Patient patient) {
		VisitType visitType = getOrCreateVisitType("Outpatient");

		Visit visit = new Visit();
		visit.setPatient(patient);
		visit.setVisitType(visitType);
		visit.setStartDatetime(new Date());
		return Context.getVisitService().saveVisit(visit);
	}

	private Encounter createEncounter(Patient patient, Visit visit) {
		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setVisit(visit);
		encounter.setEncounterType(defaultEncounterType);
		encounter.setEncounterDatetime(new Date());
		encounter.setLocation(defaultLocation);
		return Context.getEncounterService().saveEncounter(encounter);
	}

	@Test
	public void shouldReturnBedByPatient() {
		Patient patient = createPatient("PID123", "John", "A", "Doe");
		Bed bed = createBed("B101");

		Visit visit = createVisit(patient);
		Encounter encounter = createEncounter(patient, visit);

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
		Patient patient = createPatient("PID124", "Jane", "B", "Smith");
		Bed bed = createBed("B102");

		Visit visit = createVisit(patient);
		Encounter encounter = createEncounter(patient, visit);

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
		Patient patient1 = createPatient("PID125", "Alice", "C", "Jones");
		Patient patient2 = createPatient("PID126", "Bob", "D", "White");

		Visit visit1 = createVisit(patient1);
		Encounter encounter1 = createEncounter(patient1, visit1);

		Visit visit2 = createVisit(patient2);
		Encounter encounter2 = createEncounter(patient2, visit2);

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

		List<BedPatientAssignment> results = bedManagementDao.getCurrentAssignmentsByBed(bed);

		assertNotNull("Expected a list of current assignments", results);
		assertEquals(2, results.size());
	}

	@Test
	public void shouldReturnLatestBedByVisit() {
		Patient patient = createPatient("PID127", "Charlie", "E", "Brown");

		Visit visit = createVisit(patient);

		Encounter encounter1 = createEncounter(patient, visit);
		Encounter encounter2 = createEncounter(patient, visit);

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
