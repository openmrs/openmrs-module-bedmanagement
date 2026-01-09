package org.openmrs.module.bedmanagement.aop;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.ValidationException;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class BedPatientAssignmentValidatorTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private BedManagementService bedManagementService;
	
	@Before
	public void beforeAllTests() throws Exception {
		executeDataSet("testPatientsDataset.xml");
		executeDataSet("bedManagementDAOComponentTestDataset.xml");
	}
	
	@Test
	public void testExceptionThrownWhenBedAssignmentEndtimeBeforeStarttime() {
		BedPatientAssignment bpa = bedManagementService
		        .getBedPatientAssignmentByUuid("10011001-1001-1001-1001-100000000001");
		
		assertThrows(ValidationException.class, () -> {
			Date startTime = bpa.getStartDatetime();
			Date oneSecondBeforeStartTime = Date.from(startTime.toInstant().minus(1, ChronoUnit.SECONDS));
			bpa.setEndDatetime(oneSecondBeforeStartTime);
			bedManagementService.saveBedPatientAssignment(bpa);
		});
	}
	
	@Test
	public void testExceptionThrownWhenBedAssignmentEndtimeAfterVisitStopTime() {
		VisitService visitService = Context.getVisitService();
		Visit visit = visitService.getVisit(1001);
		BedPatientAssignment bpa = bedManagementService
		        .getBedPatientAssignmentByUuid("10011001-1001-1001-1001-100000000001");
		
		assertThrows(ValidationException.class, () -> {
			Date now = new Date();
			visitService.endVisit(visit, now);
			Date oneSecondAfter = Date.from(now.toInstant().plus(1, ChronoUnit.SECONDS));
			bpa.setEndDatetime(oneSecondAfter);
			bedManagementService.saveBedPatientAssignment(bpa);
		});
	}
	
	@Test
	public void testExceptionThrownWhenMultipleActiveBedsAssignedToPatient() {
		BedPatientAssignment bpa = bedManagementService
		        .getBedPatientAssignmentByUuid("10011001-1001-1001-1001-100000000001");
		Patient patient = bpa.getPatient();
		Bed bed = bedManagementService.getBedById(13);
		assertNull(bpa.getEndDatetime());
		
		assertThrows(ValidationException.class, () -> {
			BedPatientAssignment bpa2 = new BedPatientAssignment();
			bpa2.setPatient(patient);
			bpa2.setStartDatetime(new Date());
			bpa2.setEncounter(bpa.getEncounter());
			bpa2.setBed(bed);
			bedManagementService.saveBedPatientAssignment(bpa2);
		});
	}
	
	@Test
	public void testExceptionNotThrownWhenSavingEndedBedsAssignment() {
		BedPatientAssignment bpa = bedManagementService
		        .getBedPatientAssignmentByUuid("10011001-1001-1001-1001-100000000001");
		Patient patient = bpa.getPatient();
		Bed bed = bedManagementService.getBedById(13);
		bedManagementService.saveBedPatientAssignment(bpa);
		
		// No exception should be thrown when saving another bed assignment with
		// endDatetime set
		// this usually happens when merging patients
		BedPatientAssignment bpa2 = new BedPatientAssignment();
		bpa2.setPatient(patient);
		bpa2.setStartDatetime(new Date());
		bpa2.setEncounter(bpa.getEncounter());
		bpa2.setBed(bed);
		bpa2.setEndDatetime(new Date());
		bedManagementService.saveBedPatientAssignment(bpa2);
	}
}
