package org.openmrs.module.bedmanagement.aop;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ValidationException;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.BedDetails;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {
        "classpath:TestingApplicationContext.xml" }, inheritLocations = true)
public class EncounterWithBedPatientAssignmentValidatorTest extends BaseModuleWebContextSensitiveTest {
	
	@Autowired
	private BedManagementService bedManagementService;
	
	@Before
	public void beforeAllTests() throws Exception {
		executeDataSet("testPatientsDataset.xml");
		executeDataSet("bedManagementDAOComponentTestDataset.xml");
	}
	
	@Test
	public void testExceptionThrownWhenEncounterSavedWithEncounterTimeAfterBedAssignmentStarttime() {
		VisitService visitService = Context.getVisitService();
		PatientService patientService = Context.getPatientService();
		EncounterService encounterService = Context.getEncounterService();
		
		Patient patient = patientService.getPatient(1001);
		Visit visit = visitService.getVisit(1001);
		Encounter encounter = encounterService.getEncounter(1001);
		BedPatientAssignment bpa = bedManagementService
		        .getBedPatientAssignmentByUuid("10011001-1001-1001-1001-100000000001");
		
		BedDetails bedDetails = bedManagementService.getBedAssignmentDetailsByPatient(patient);
		
		assertThat("Invalid test data, patient has no bed assigned", bedDetails, is(notNullValue()));
		
		assertThrows(ValidationException.class, () -> {
			Date oneSecondAfterStartingBedAssignment = Date
			        .from(bpa.getStartDatetime().toInstant().plus(1, ChronoUnit.SECONDS));
			encounter.setEncounterDatetime(oneSecondAfterStartingBedAssignment);
			encounterService.saveEncounter(encounter);
		});
	}
}
