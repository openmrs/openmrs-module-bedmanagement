package org.openmrs.module.bedmanagement.atomfeed;

import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsQueueJdbcImpl;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventServiceImpl;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.module.bedmanagement.entity.BedTagMap;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BedTagMapAdviceTest {
	
	private BedTagMapAdvice bedTagMapAdvice;
	
	private AtomFeedSpringTransactionManager atomFeedSpringTransactionManager;
	
	@Mock
	private BedTagMap bedTagMap;
	
	@Mock
	private AdministrationService administrationService;
	
	@Mock
	private PlatformTransactionManager platformTransactionManager;
	
	@Mock
	private AllEventRecordsQueueJdbcImpl allEventRecordsQueue;
	
	@Mock
	private EventServiceImpl eventService;
	
	@Mock
	private Event event;
	
	private static final String BED_TAG_MAP_EVENT_RECORD_GLOBAL_PROPERTY = "atomfeed.publish.eventsForBedTagMapChange";
	
	private static final String BED_TAG_MAP_EVENT_URL_PATTERN_GLOBAL_PROPERTY = "atomfeed.event.urlPatternForBedTagMap";
	
	private static final String DEFAULT_BED_TAG_MAP_EVENT_URL_PATTERN = "/openmrs/ws/rest/v1/bedTagMap/{uuid}";
	
	private static final String SOME_UUID = "SOME-UUID";
	
	private static final String DEFAULT_BED_TAG_MAP_EVENT_URL_PATTERN_AFTER_UUID_REPLACE = "/openmrs/ws/rest/v1/bedTagMap/"
	        + SOME_UUID;
	
	private static final String CATEGORY = "bedtagmap";
	
	private static final String TITLE = "Bed-Tag-Map";
	
	private MockedStatic<Context> contextStaticMock;
	
	private MockedConstruction<AtomFeedSpringTransactionManager> atomFeedTxMgrConstruction;
	
	private MockedConstruction<AllEventRecordsQueueJdbcImpl> queueConstruction;
	
	private MockedConstruction<EventServiceImpl> eventServiceConstruction;
	
	private MockedConstruction<Event> eventConstruction;
	
	@BeforeEach
	public void setUp() throws Exception {
		mockStatic(Context.class);
		when(Context.getRegisteredComponents(PlatformTransactionManager.class))
		        .thenReturn(Collections.singletonList(platformTransactionManager));
		when(Context.getAdministrationService()).thenReturn(administrationService);
		when(administrationService.getGlobalProperty(anyString())).thenReturn("true");
		when(administrationService.getGlobalProperty(anyString(), anyString()))
		        .thenReturn(DEFAULT_BED_TAG_MAP_EVENT_URL_PATTERN);
		atomFeedSpringTransactionManager = spy(new AtomFeedSpringTransactionManager(platformTransactionManager));
		atomFeedTxMgrConstruction = mockConstruction(AtomFeedSpringTransactionManager.class,
		    (mock, context) -> atomFeedSpringTransactionManager = spy(mock));
		
		queueConstruction = mockConstruction(AllEventRecordsQueueJdbcImpl.class,
		    (mock, context) -> allEventRecordsQueue = mock);
		
		eventServiceConstruction = mockConstruction(EventServiceImpl.class, (mock, context) -> eventService = mock);
		
		eventConstruction = mockConstruction(Event.class, (mock, context) -> event = mock);
		
		when(bedTagMap.getUuid()).thenReturn(SOME_UUID);
		
		bedTagMapAdvice = new BedTagMapAdvice();
	}
	
	@AfterEach
	void tearDown() {
		if (contextStaticMock != null) {
			contextStaticMock.close();
		}
		if (atomFeedTxMgrConstruction != null) {
			atomFeedTxMgrConstruction.close();
		}
		if (queueConstruction != null) {
			queueConstruction.close();
		}
		if (eventServiceConstruction != null) {
			eventServiceConstruction.close();
		}
		if (eventConstruction != null) {
			eventConstruction.close();
		}
	}
	
	private void verifyAssertsForRaisingEvents() throws Exception {
		Context.getRegisteredComponents(PlatformTransactionManager.class);
		Context.getAdministrationService();
		verify(administrationService, times(1)).getGlobalProperty(eq(BED_TAG_MAP_EVENT_RECORD_GLOBAL_PROPERTY));
		verify(administrationService, times(1)).getGlobalProperty(eq(BED_TAG_MAP_EVENT_URL_PATTERN_GLOBAL_PROPERTY),
		    eq(DEFAULT_BED_TAG_MAP_EVENT_URL_PATTERN));
		verify(bedTagMap, times(1)).getUuid();
		assertEquals(1, eventConstruction.constructed().size());
		verify(atomFeedSpringTransactionManager, times(1)).executeWithTransaction(any(AFTransactionWorkWithoutResult.class));
		verify(eventService, times(1)).notify(any(Event.class));
	}
	
	private void verifyAssertsForNotRaisingEvents() throws Exception {
		Context.getRegisteredComponents(PlatformTransactionManager.class);
		Context.getAdministrationService();
		verify(administrationService, times(1)).getGlobalProperty(eq(BED_TAG_MAP_EVENT_RECORD_GLOBAL_PROPERTY));
		verify(administrationService, times(0)).getGlobalProperty(eq(BED_TAG_MAP_EVENT_URL_PATTERN_GLOBAL_PROPERTY),
		    eq(DEFAULT_BED_TAG_MAP_EVENT_URL_PATTERN));
		verify(bedTagMap, times(0)).getUuid();
		assertEquals(0, eventConstruction.constructed().size());
		verify(atomFeedSpringTransactionManager, times(0)).executeWithTransaction(any(AFTransactionWorkWithoutResult.class));
		verify(eventService, times(0)).notify(any(Event.class));
	}
	
	@Test
	public void shouldRaiseEventForBedTagMapChange() throws Exception {
		
		bedTagMapAdvice.afterReturning(bedTagMap, this.getClass().getMethod("save"), null, null);
		
		verifyAssertsForRaisingEvents();
	}
	
	@Test
	public void shouldRaiseEventIfEventGlobalPropertyIsEmpty() throws Exception {
		when(administrationService.getGlobalProperty(anyString())).thenReturn("");
		
		bedTagMapAdvice.afterReturning(bedTagMap, this.getClass().getMethod("save"), null, null);
		
		verifyAssertsForRaisingEvents();
	}
	
	@Test
	public void shouldNotRaiseEventIfEventGlobalPropertyIsFalse() throws Exception {
		when(administrationService.getGlobalProperty(anyString())).thenReturn("false");
		
		bedTagMapAdvice.afterReturning(bedTagMap, this.getClass().getMethod("save"), null, null);
		
		verifyAssertsForNotRaisingEvents();
	}
	
	@Test
	public void shouldNotRaiseEventIfMethodIsNotSaveAndDelete() throws Exception {
		
		bedTagMapAdvice.afterReturning(bedTagMap, this.getClass().getMethod("someOtherMethod"), null, null);
		
		verifyAssertsForNotRaisingEvents();
	}
	
	@Test
	public void shouldRaiseEventUsingBedTagMapFromParametersIfReturnValueIsNull() throws Exception {
		Object[] parameters = new Object[] { bedTagMap };
		bedTagMapAdvice.afterReturning(null, this.getClass().getMethod("delete"), parameters, null);
		
		verifyAssertsForRaisingEvents();
	}
	
	public void save() {
	}
	
	public void delete() {
	}
	
	public void someOtherMethod() {
	}
}
