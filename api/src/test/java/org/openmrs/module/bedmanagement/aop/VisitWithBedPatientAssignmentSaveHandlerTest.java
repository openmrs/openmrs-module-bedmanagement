package org.openmrs.module.bedmanagement.aop;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.BedDetails;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class VisitWithBedPatientAssignmentSaveHandlerTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private BedManagementService bedManagementService;
	
	@Before
	public void beforeAllTests() throws Exception {
		executeDataSet("testPatientsDataset.xml");
		executeDataSet("bedManagementDAOComponentTestDataset.xml");
	}
	
	@Test
	public void testBedAssignmentEndsWhenisitEnds() {
		VisitService visitService = Context.getVisitService();
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(1001);
		Visit visit = visitService.getVisit(1001);
		
		BedDetails bedDetails = bedManagementService.getBedAssignmentDetailsByPatient(patient);
		
		assertThat("Invalid test data, patient has no bed assigned", bedDetails, is(notNullValue()));
		Date now = new Date();
		visit.setStopDatetime(now);
		visitService.endVisit(visit, now);
		
		BedDetails updatedBedDetails = bedManagementService.getBedAssignmentDetailsByPatient(patient);
		assertThat("Bed failed to unassign when corresponding visit ends", updatedBedDetails, is(nullValue()));
	}
}
