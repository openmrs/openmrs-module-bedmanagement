package org.openmrs.module.bedmanagement.aop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.openmrs.Encounter;
import org.openmrs.User;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.service.BedManagementService;

/**
 * Unit tests for {@link EncounterWithBedPatientAssignmentSaveHandler}.
 * <p>
 * The handler runs inside the OpenMRS save pipeline, so the bed assignments it touches are already
 * persistent and attached to the current Hibernate session. It must mutate them in place and let
 * the surrounding transaction flush the changes. It must NOT call the {@code @Transactional}
 * {@code saveBedPatientAssignment} / {@code deleteBedPatientAssignment} service methods: their
 * commit forced an early flush of the whole session, which could trip over unrelated,
 * not-yet-populated entities (e.g. a {@code VisitAttribute} whose NOT NULL {@code valueReference}
 * had not been serialized yet) and abort the save with a {@code DataIntegrityViolationException}.
 */
@ExtendWith(MockitoExtension.class)
public class EncounterWithBedPatientAssignmentSaveHandlerUnitTest {
	
	@Mock
	private BedManagementService bedManagementService;
	
	private EncounterWithBedPatientAssignmentSaveHandler handler;
	
	private final User user = new User();
	
	@BeforeEach
	public void setUp() {
		handler = new EncounterWithBedPatientAssignmentSaveHandler(bedManagementService);
	}
	
	@Test
	public void handleShouldUpdateAssignmentStartDatetimeInPlaceWithoutCallingTransactionalSave() {
		Date encounterDatetime = new Date();
		Encounter encounter = new Encounter();
		encounter.setEncounterId(1001);
		encounter.setUuid("enc-uuid");
		encounter.setEncounterDatetime(encounterDatetime);
		
		BedPatientAssignment bpa = new BedPatientAssignment();
		when(bedManagementService.getBedPatientAssignmentByEncounter("enc-uuid", true))
		        .thenReturn(Collections.singletonList(bpa));
		
		handler.handle(encounter, user, new Date(), null);
		
		assertEquals(encounterDatetime, bpa.getStartDatetime());
		// the fix: the assignment is mutated in place, never re-saved through the
		// transactional service
		verify(bedManagementService, never()).saveBedPatientAssignment(any());
	}
	
	@Test
	public void handleShouldVoidAssignmentsInPlaceWhenEncounterVoidedWithoutCallingTransactionalDelete() {
		Encounter encounter = new Encounter();
		encounter.setEncounterId(1001);
		encounter.setUuid("enc-uuid");
		encounter.setEncounterDatetime(new Date());
		encounter.setVoided(true);
		
		BedPatientAssignment bpa = new BedPatientAssignment();
		when(bedManagementService.getBedPatientAssignmentByEncounter("enc-uuid", true))
		        .thenReturn(Collections.singletonList(bpa));
		
		Date voidDate = new Date();
		handler.handle(encounter, user, voidDate, null);
		
		assertTrue(bpa.getVoided());
		assertEquals("encounter voided", bpa.getVoidReason());
		assertEquals(user, bpa.getVoidedBy());
		assertEquals(voidDate, bpa.getDateVoided());
		// the fix: the assignment is voided in place, never routed through the
		// transactional service
		verify(bedManagementService, never()).deleteBedPatientAssignment(any(), anyString());
		verify(bedManagementService, never()).saveBedPatientAssignment(any());
	}
	
	@Test
	public void handleShouldDoNothingForTransientEncounter() {
		Encounter encounter = new Encounter(); // no encounterId -> not yet persisted
		
		handler.handle(encounter, user, new Date(), null);
		
		verifyNoInteractions(bedManagementService);
	}
}
