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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BedTagMapAdviceTest {
	
	private BedTagMapAdvice bedTagMapAdvice;
	
	@Mock
	private BedTagMap bedTagMap;
	
	@Mock
	private AdministrationService administrationService;
	
	@Mock
	private PlatformTransactionManager platformTransactionManager;
	
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
	
	// A hack to resolve the error when Mocking the Context :)
	private Logger logger = LoggerFactory.getLogger(BedTagMapAdviceTest.class);
	
	@BeforeEach
	public void setUp() {
		contextStaticMock = mockStatic(Context.class);
		
		contextStaticMock.when(() -> Context.getRegisteredComponents(PlatformTransactionManager.class))
		        .thenReturn(Collections.singletonList(platformTransactionManager));
		contextStaticMock.when(Context::getAdministrationService).thenReturn(administrationService);
		lenient().when(administrationService.getGlobalProperty(eq(BED_TAG_MAP_EVENT_RECORD_GLOBAL_PROPERTY)))
		        .thenReturn("true");
		lenient()
		        .when(
		            administrationService.getGlobalProperty(eq(BED_TAG_MAP_EVENT_URL_PATTERN_GLOBAL_PROPERTY), anyString()))
		        .thenReturn(DEFAULT_BED_TAG_MAP_EVENT_URL_PATTERN);
		atomFeedTxMgrConstruction = mockConstruction(AtomFeedSpringTransactionManager.class, (mock, context) -> {
			when(mock.executeWithTransaction(any(AFTransactionWorkWithoutResult.class))).thenAnswer(invocation -> {
				AFTransactionWorkWithoutResult work = invocation.getArgument(0);
				try {
					java.lang.reflect.Method method = AFTransactionWorkWithoutResult.class
					        .getDeclaredMethod("doInTransaction");
					method.setAccessible(true);
					method.invoke(work);
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
				return null;
			});
		});
		
		queueConstruction = mockConstruction(AllEventRecordsQueueJdbcImpl.class);
		
		eventServiceConstruction = mockConstruction(EventServiceImpl.class);
		
		eventConstruction = mockConstruction(Event.class);
		
		lenient().when(bedTagMap.getUuid()).thenReturn(SOME_UUID);
		
		bedTagMapAdvice = new BedTagMapAdvice();
	}
	
	@AfterEach
	void tearDown() {
		contextStaticMock.close();
		atomFeedTxMgrConstruction.close();
		queueConstruction.close();
		eventServiceConstruction.close();
		eventConstruction.close();
	}
	
	private void verifyAssertsForRaisingEvents() {
		verify(administrationService, times(1)).getGlobalProperty(eq(BED_TAG_MAP_EVENT_RECORD_GLOBAL_PROPERTY));
		verify(administrationService, times(1)).getGlobalProperty(eq(BED_TAG_MAP_EVENT_URL_PATTERN_GLOBAL_PROPERTY),
		    anyString());
		verify(bedTagMap, times(1)).getUuid();
		assertEquals(1, eventConstruction.constructed().size());
		assertEquals(1, atomFeedTxMgrConstruction.constructed().size());
		verify(atomFeedTxMgrConstruction.constructed().get(0), times(1))
		        .executeWithTransaction(any(AFTransactionWorkWithoutResult.class));
		assertEquals(1, eventServiceConstruction.constructed().size());
		verify(eventServiceConstruction.constructed().get(0), times(1)).notify(any(Event.class));
	}
	
	private void verifyAssertsForNotRaisingEvents() {
		verify(administrationService, times(1)).getGlobalProperty(eq(BED_TAG_MAP_EVENT_RECORD_GLOBAL_PROPERTY));
		verify(administrationService, times(0)).getGlobalProperty(eq(BED_TAG_MAP_EVENT_URL_PATTERN_GLOBAL_PROPERTY),
		    anyString());
		verify(bedTagMap, times(0)).getUuid();
		assertEquals(0, eventConstruction.constructed().size());
	}
	
	@Test
	public void shouldRaiseEventForBedTagMapChange() throws Exception {
		
		bedTagMapAdvice.afterReturning(bedTagMap, this.getClass().getMethod("save"), null, null);
		
		verifyAssertsForRaisingEvents();
	}
	
	@Test
	public void shouldRaiseEventIfEventGlobalPropertyIsEmpty() throws Exception {
		when(administrationService.getGlobalProperty(eq(BED_TAG_MAP_EVENT_RECORD_GLOBAL_PROPERTY))).thenReturn("");
		
		bedTagMapAdvice.afterReturning(bedTagMap, this.getClass().getMethod("save"), null, null);
		
		verifyAssertsForRaisingEvents();
	}
	
	@Test
	public void shouldNotRaiseEventIfEventGlobalPropertyIsFalse() throws Exception {
		when(administrationService.getGlobalProperty(eq(BED_TAG_MAP_EVENT_RECORD_GLOBAL_PROPERTY))).thenReturn("false");
		
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
