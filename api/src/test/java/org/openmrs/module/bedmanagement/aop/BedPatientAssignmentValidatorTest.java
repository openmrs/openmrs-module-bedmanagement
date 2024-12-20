package org.openmrs.module.bedmanagement.aop;

import static org.junit.Assert.assertThrows;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Visit;
import org.openmrs.api.ValidationException;
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
public class BedPatientAssignmentValidatorTest extends BaseModuleWebContextSensitiveTest {
	
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
}
