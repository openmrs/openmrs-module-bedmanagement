package org.openmrs.module.bedmanagement.aop;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.service.BedManagementService;

/**
 * Unit tests for {@link VisitWithBedPatientAssignmentSaveHandler}.
 * <p>
 * {@code unAssignBedsInEndedVisit} requires "Assign Beds" and "Edit Admission Locations". The
 * handler runs on every save of an ended visit, so it must only call it when the visit actually has
 * an active bed assignment to end; otherwise ending a visit of a patient who never had a bed would
 * demand bed write privileges from every user who can end a visit.
 */
@ExtendWith(MockitoExtension.class)
public class VisitWithBedPatientAssignmentSaveHandlerUnitTest {

	@Mock
	private BedManagementService bedManagementService;

	private VisitWithBedPatientAssignmentSaveHandler handler;

	private final User user = new User();

	@BeforeEach
	public void setUp() {
		handler = new VisitWithBedPatientAssignmentSaveHandler(bedManagementService);
	}

	private Visit visit(Date stopDatetime) {
		Visit visit = new Visit();
		visit.setVisitId(1001);
		visit.setUuid("visit-uuid");
		visit.setStopDatetime(stopDatetime);
		return visit;
	}

	@Test
	public void handleShouldNotUnassignBedsWhenTheEndedVisitHasNoActiveBedAssignment() {
		Visit visit = visit(new Date());
		when(bedManagementService.getBedPatientAssignmentByVisit("visit-uuid", false))
		        .thenReturn(Collections.<BedPatientAssignment> emptyList());

		handler.handle(visit, user, new Date(), null);

		verify(bedManagementService, never()).unAssignBedsInEndedVisit(any(Visit.class));
	}

	@Test
	public void handleShouldUnassignBedsWhenTheEndedVisitHasAnActiveBedAssignment() {
		Visit visit = visit(new Date());
		when(bedManagementService.getBedPatientAssignmentByVisit("visit-uuid", false))
		        .thenReturn(Collections.singletonList(new BedPatientAssignment()));

		handler.handle(visit, user, new Date(), null);

		verify(bedManagementService).unAssignBedsInEndedVisit(visit);
	}

	@Test
	public void handleShouldDoNothingForAVisitThatHasNotEnded() {
		handler.handle(visit(null), user, new Date(), null);

		verifyNoInteractions(bedManagementService);
	}

	@Test
	public void handleShouldDoNothingForANewVisit() {
		Visit visit = new Visit();
		visit.setStopDatetime(new Date());

		handler.handle(visit, user, new Date(), null);

		verifyNoInteractions(bedManagementService);
	}
}
