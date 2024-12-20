package org.openmrs.module.bedmanagement.aop;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(locations = {
        "classpath:TestingApplicationContext.xml" }, inheritLocations = true)
public class EncounterWithBedPatientAssignmentSaveHandlerTest extends BaseModuleWebContextSensitiveTest {
	
	@Autowired
	private BedManagementService bedManagementService;
	
	@Before
	public void beforeAllTests() throws Exception {
		executeDataSet("testPatientsDataset.xml");
		executeDataSet("bedManagementDAOComponentTestDataset.xml");
	}
	
	@Test
	public void testBedAssignmentVoidedWhenVisitIsVoided() {
		// voiding a visit should transitively void its encounters
		// which triggers EncounterWithBedPatientAssignmentSaveHandler
		// to void its bed assignments
		
		VisitService visitService = Context.getVisitService();
		Visit visit = visitService.getVisit(1001);
		BedPatientAssignment bpa = bedManagementService
		        .getBedPatientAssignmentByUuid("10011001-1001-1001-1001-100000000001");
		assertThat("bed patient assignment should initially not bed void", bpa.getVoided(), is(false));
		
		visit = visitService.voidVisit(visit, "for testing");
		assertThat(visit.getVoided(), is(true));
		
		for (Encounter encounter : visit.getEncounters()) {
			assertThat(encounter.getVoided(), is(true));
		}
		
		BedPatientAssignment updatedBpa = bedManagementService.getBedPatientAssignmentByUuid(bpa.getUuid());
		assertThat("bed patient assignment should be void after voiding visit", updatedBpa.getVoided(), is(true));
	}
	
	@Test
	public void testBedAssisgnmentStartDatetimeChangeWhenEncounterTimeChanges() {
		EncounterService encounterService = Context.getEncounterService();
		Encounter encounter = encounterService.getEncounter(1001);
		Date encounterTime = encounter.getEncounterDatetime();
		BedPatientAssignment bpa = bedManagementService
		        .getBedPatientAssignmentByUuid("10011001-1001-1001-1001-100000000001");
		
		Date oneSecondAfter = Date.from(encounterTime.toInstant().plus(1, ChronoUnit.SECONDS));
		encounter.setEncounterDatetime(oneSecondAfter);
		encounterService.saveEncounter(encounter);
		
		BedPatientAssignment updatedBpa = bedManagementService.getBedPatientAssignmentByUuid(bpa.getUuid());
		assertThat("bed patient assignment start datetime should change with encounter time", updatedBpa.getStartDatetime(),
		    equalTo(oneSecondAfter));
		
		Date oneSecondBefore = Date.from(encounterTime.toInstant().minus(1, ChronoUnit.SECONDS));
		encounter.setEncounterDatetime(oneSecondBefore);
		encounterService.saveEncounter(encounter);
		
		BedPatientAssignment updatedBpa2 = bedManagementService.getBedPatientAssignmentByUuid(bpa.getUuid());
		assertThat("bed patient assignment start datetime should change with encounter time", updatedBpa2.getStartDatetime(),
		    equalTo(oneSecondBefore));
	}
}
