package org.openmrs.module.bedmanagement.aop;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;

import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.UsernamePasswordCredentials;

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
	
	/**
	 * Signs in as a user with no role, holding only what ending a visit needs outside bed management,
	 * plus the bed READ privileges the visit validator checks: no "Assign Beds" and no "Edit Admission
	 * Locations".
	 */
	private void becomeUserWithoutBedWritePrivileges() {
		Person person = new Person();
		person.setGender("F");
		person.addName(new PersonName("Records", null, "Officer"));
		User user = new User(person);
		user.setUsername("records-officer");
		Context.getUserService().createUser(user, "Records0fficer");
		Context.logout();
		Context.authenticate(new UsernamePasswordCredentials("records-officer", "Records0fficer"));
		for (String privilege : new String[] { "Edit Visits", "Get Visits", "Get Visit Types", "Get Visit Attribute Types",
		        "Get Patients", "Get Encounters", "Get Locations", "Get Concepts", "Get Global Properties", "Get Beds",
		        "Get Admission Locations" }) {
			Context.addProxyPrivilege(privilege);
		}
	}
	
	@Test
	public void endingAVisitWithNoBedAssignmentShouldNotRequireBedWritePrivileges() {
		VisitService visitService = Context.getVisitService();
		Patient patient = Context.getPatientService().getPatient(1001);
		// as admin, take the patient out of the bed the dataset gives this visit, so it
		// has none left
		bedManagementService.unAssignPatientFromBed(patient);
		assertThat("Invalid test data, patient still has a bed",
		    bedManagementService.getBedAssignmentDetailsByPatient(patient), is(nullValue()));
		Context.flushSession();
		
		becomeUserWithoutBedWritePrivileges();
		visitService.endVisit(visitService.getVisit(1001), new Date());
		
		assertThat(visitService.getVisit(1001).getStopDatetime(), is(notNullValue()));
	}
	
	@Test(expected = APIAuthenticationException.class)
	public void endingAVisitWithABedAssignmentShouldStillRequireBedWritePrivileges() {
		VisitService visitService = Context.getVisitService();
		assertThat("Invalid test data, patient has no bed assigned",
		    bedManagementService.getBedAssignmentDetailsByPatient(Context.getPatientService().getPatient(1001)),
		    is(notNullValue()));
		
		becomeUserWithoutBedWritePrivileges();
		visitService.endVisit(visitService.getVisit(1001), new Date());
	}
	
	/**
	 * Control for the test above: the same user ends the same visit once given the two bed write
	 * privileges, so those, and nothing else, are what it was refused for.
	 */
	@Test
	public void endingAVisitWithABedAssignmentShouldFreeTheBedForAUserWithBedWritePrivileges() {
		VisitService visitService = Context.getVisitService();
		Patient patient = Context.getPatientService().getPatient(1001);
		
		becomeUserWithoutBedWritePrivileges();
		Context.addProxyPrivilege("Assign Beds");
		Context.addProxyPrivilege("Edit Admission Locations");
		visitService.endVisit(visitService.getVisit(1001), new Date());
		
		assertThat(bedManagementService.getBedAssignmentDetailsByPatient(patient), is(nullValue()));
	}
}
