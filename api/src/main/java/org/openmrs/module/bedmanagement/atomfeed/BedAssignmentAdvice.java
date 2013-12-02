package org.openmrs.module.bedmanagement.atomfeed;

import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.ict4h.atomfeed.server.service.EventServiceImpl;
import org.joda.time.DateTime;
import org.openmrs.module.bedmanagement.BedDetails;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;
import java.net.URI;
import java.sql.SQLException;
import java.util.UUID;

public class BedAssignmentAdvice implements AfterReturningAdvice {

    private static final String TEMPLATE = "/openmrs/ws/rest/v1/bedPatientAssignment/%s?v=full";
    public static final String CATEGORY = "encounter";
    public static final String TITLE = "Bed-Assignment";
    private static final String ASSIGN_BED_METHOD = "assignPatientToBed";

    private EventService eventService;

    public BedAssignmentAdvice() throws SQLException {
        AllEventRecordsJdbcImpl records = new AllEventRecordsJdbcImpl(new OpenMRSConnectionProvider());
        this.eventService = new EventServiceImpl(records);
    }

    public BedAssignmentAdvice(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        if (method.getName().equals(ASSIGN_BED_METHOD)) {
            String contents = String.format(TEMPLATE, ((BedDetails) returnValue).getBed().getBedPatientAssignment().iterator().next().getUuid());
            Event event = new Event(UUID.randomUUID().toString(), TITLE, DateTime.now(), (URI) null, contents, CATEGORY);
            eventService.notify(event);
        }
    }
}
