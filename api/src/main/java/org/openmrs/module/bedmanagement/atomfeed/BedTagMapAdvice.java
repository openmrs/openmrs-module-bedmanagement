package org.openmrs.module.bedmanagement.atomfeed;

import org.apache.commons.lang.StringUtils;
import org.ict4h.atomfeed.server.repository.AllEventRecordsQueue;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsQueueJdbcImpl;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.ict4h.atomfeed.server.service.EventServiceImpl;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.joda.time.DateTime;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.module.bedmanagement.entity.BedTagMap;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.UUID;

public class BedTagMapAdvice implements AfterReturningAdvice {
	
	private static final String BED_TAG_MAP_EVENT_RECORD_GLOBAL_PROPERTY = "atomfeed.publish.eventsForBedTagMapChange";
	
	private static final String BED_TAG_MAP_EVENT_URL_PATTERN_GLOBAL_PROPERTY = "atomfeed.event.urlPatternForBedTagMap";
	
	private static final String DEFAULT_BED_TAG_MAP_EVENT_URL_PATTERN = "/openmrs/ws/rest/v1/bedTagMap/{uuid}";
	
	private static final String CATEGORY = "bedtagmap";
	
	private static final String TITLE = "Bed-Tag-Map";
	
	private static final String SAVE_METHOD = "save";
	
	private static final String DELETE_METHOD = "delete";
	
	private static final int BED_TAG_MAP_OBJECT_INDEX = 0;
	
	private static final int FIRST_TRANSACTION_MANAGER_INDEX = 0;
	
	private static final String UUID_PATTERN_TO_REPLACE = "{uuid}";
	
	private AtomFeedSpringTransactionManager atomFeedSpringTransactionManager;
	
	private EventService eventService;
	
	public BedTagMapAdvice() {
		atomFeedSpringTransactionManager = new AtomFeedSpringTransactionManager(getSpringPlatformTransactionManager());
		AllEventRecordsQueue allEventRecordsQueue = new AllEventRecordsQueueJdbcImpl(atomFeedSpringTransactionManager);
		eventService = new EventServiceImpl(allEventRecordsQueue);
	}
	
	@Override
	public void afterReturning(Object returnValue, Method method, Object[] parameters, Object o1) {
		if (!shouldRaiseRelationshipEvent()) {
			return;
		}
		String execMethodName = method.getName();
		if (!SAVE_METHOD.equals(execMethodName) && !DELETE_METHOD.equals(execMethodName)) {
			return;
		}
		raiseBedTagMapEvent(returnValue, parameters);
	}
	
	private void raiseBedTagMapEvent(Object returnValue, Object[] parameters) {
		BedTagMap returnValueToRecord = returnValue == null ? (BedTagMap) parameters[BED_TAG_MAP_OBJECT_INDEX]
		        : (BedTagMap) returnValue;
		Event bedTagMapEvent = getBedTagMapEvent(returnValueToRecord);
		recordEvent(bedTagMapEvent);
	}
	
	private Object recordEvent(final Event bedTagMapEvent) {
		return atomFeedSpringTransactionManager.executeWithTransaction(new AFTransactionWorkWithoutResult() {
			
			@Override
			protected void doInTransaction() {
				eventService.notify(bedTagMapEvent);
			}
			
			@Override
			public PropagationDefinition getTxPropagationDefinition() {
				return PropagationDefinition.PROPAGATION_REQUIRED;
			}
		});
	}
	
	private boolean shouldRaiseRelationshipEvent() {
		String raiseEvent = Context.getAdministrationService().getGlobalProperty(BED_TAG_MAP_EVENT_RECORD_GLOBAL_PROPERTY);
		return StringUtils.isEmpty(raiseEvent) || Boolean.valueOf(raiseEvent);
	}
	
	private Event getBedTagMapEvent(final BedTagMap returnValueToRecord) {
		String contents = getUrlPattern().replace(UUID_PATTERN_TO_REPLACE, returnValueToRecord.getUuid());
		return new Event(UUID.randomUUID().toString(), TITLE, null, (URI) null, contents, CATEGORY);
	}
	
	private String getUrlPattern() {
		return Context.getAdministrationService().getGlobalProperty(BED_TAG_MAP_EVENT_URL_PATTERN_GLOBAL_PROPERTY,
		    DEFAULT_BED_TAG_MAP_EVENT_URL_PATTERN);
	}
	
	private PlatformTransactionManager getSpringPlatformTransactionManager() {
		return Context.getRegisteredComponents(PlatformTransactionManager.class).get(FIRST_TRANSACTION_MANAGER_INDEX);
	}
	
}
