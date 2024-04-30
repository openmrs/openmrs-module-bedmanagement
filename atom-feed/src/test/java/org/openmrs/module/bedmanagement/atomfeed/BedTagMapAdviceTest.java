package org.openmrs.module.bedmanagement.atomfeed;

import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsQueueJdbcImpl;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventServiceImpl;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.module.bedmanagement.entity.BedTagMap;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.transaction.PlatformTransactionManager;

import java.net.URI;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, BedTagMapAdvice.class })
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
	
	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(Context.class);
		when(Context.getRegisteredComponents(PlatformTransactionManager.class))
		        .thenReturn(Collections.singletonList(platformTransactionManager));
		when(Context.getAdministrationService()).thenReturn(administrationService);
		when(administrationService.getGlobalProperty(anyString())).thenReturn("true");
		when(administrationService.getGlobalProperty(anyString(), anyString()))
		        .thenReturn(DEFAULT_BED_TAG_MAP_EVENT_URL_PATTERN);
		atomFeedSpringTransactionManager = spy(new AtomFeedSpringTransactionManager(platformTransactionManager));
		whenNew(AtomFeedSpringTransactionManager.class).withArguments(platformTransactionManager)
		        .thenReturn(atomFeedSpringTransactionManager);
		whenNew(AllEventRecordsQueueJdbcImpl.class).withArguments(atomFeedSpringTransactionManager)
		        .thenReturn(allEventRecordsQueue);
		whenNew(EventServiceImpl.class).withArguments(allEventRecordsQueue).thenReturn(eventService);
		whenNew(Event.class).withAnyArguments().thenReturn(event);
		when(bedTagMap.getUuid()).thenReturn(SOME_UUID);
		
		bedTagMapAdvice = new BedTagMapAdvice();
	}
	
	private void verifyAssertsForRaisingEvents() throws Exception {
		Context.getRegisteredComponents(PlatformTransactionManager.class);
		Context.getAdministrationService();
		verify(administrationService, times(1)).getGlobalProperty(eq(BED_TAG_MAP_EVENT_RECORD_GLOBAL_PROPERTY));
		verify(administrationService, times(1)).getGlobalProperty(eq(BED_TAG_MAP_EVENT_URL_PATTERN_GLOBAL_PROPERTY),
		    eq(DEFAULT_BED_TAG_MAP_EVENT_URL_PATTERN));
		verify(bedTagMap, times(1)).getUuid();
		verifyNew(Event.class, times(1)).withArguments(anyString(), eq(TITLE), any(), any(),
		    eq(DEFAULT_BED_TAG_MAP_EVENT_URL_PATTERN_AFTER_UUID_REPLACE), eq(CATEGORY));
		verify(atomFeedSpringTransactionManager, times(1)).executeWithTransaction(any(AFTransactionWorkWithoutResult.class));
		verify(eventService, times(1)).notify(event);
	}
	
	private void verifyAssertsForNotRaisingEvents() throws Exception {
		Context.getRegisteredComponents(PlatformTransactionManager.class);
		Context.getAdministrationService();
		verify(administrationService, times(1)).getGlobalProperty(eq(BED_TAG_MAP_EVENT_RECORD_GLOBAL_PROPERTY));
		verify(administrationService, times(0)).getGlobalProperty(eq(BED_TAG_MAP_EVENT_URL_PATTERN_GLOBAL_PROPERTY),
		    eq(DEFAULT_BED_TAG_MAP_EVENT_URL_PATTERN));
		verify(bedTagMap, times(0)).getUuid();
		verifyNew(Event.class, times(0)).withArguments(anyString(), eq(TITLE), any(Date.class), any(URI.class),
		    eq(DEFAULT_BED_TAG_MAP_EVENT_URL_PATTERN_AFTER_UUID_REPLACE), eq(CATEGORY));
		verify(atomFeedSpringTransactionManager, times(0)).executeWithTransaction(any(AFTransactionWorkWithoutResult.class));
		verify(eventService, times(0)).notify(event);
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
