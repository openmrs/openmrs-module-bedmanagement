package org.openmrs.module.bedmanagement.aop;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.ValidationException;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.BedDetails;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class VisitWithBedPatientAssignmentValidatorTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private BedManagementService bedManagementService;
	
	@Before
	public void beforeAllTests() throws Exception {
		executeDataSet("testPatientsDataset.xml");
		executeDataSet("bedManagementDAOComponentTestDataset.xml");
	}
	
	@Test
	public void testExceptionThrownWhenVisitSavedWithEndtimeBeforeBedAssignmentEndtime() {
		VisitService visitService = Context.getVisitService();
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(1001);
		Visit visit = visitService.getVisit(1001);
		
		BedDetails bedDetails = bedManagementService.getBedAssignmentDetailsByPatient(patient);
		
		assertThat("Invalid test data, patient has no bed assigned", bedDetails, is(notNullValue()));
		
		BedDetails updatedBedDetails = bedManagementService.unAssignPatientFromBed(patient);
		BedPatientAssignment endedAssignment = updatedBedDetails.getLastAssignment();
		
		assertThrows(ValidationException.class, () -> {
			Date oneSecondBeforeEndingBedAssignment = Date
			        .from(endedAssignment.getEndDatetime().toInstant().minus(1, ChronoUnit.SECONDS));
			visitService.endVisit(visit, oneSecondBeforeEndingBedAssignment);
		});
	}
}
