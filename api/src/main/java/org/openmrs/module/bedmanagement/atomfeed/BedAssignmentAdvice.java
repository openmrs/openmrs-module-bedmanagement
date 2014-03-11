package org.openmrs.module.bedmanagement.atomfeed;

import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.ict4h.atomfeed.server.service.EventServiceImpl;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.joda.time.DateTime;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.module.bedmanagement.BedDetails;
import org.openmrs.module.bedmanagement.BedPatientAssignment;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Method;
import java.net.URI;
import java.sql.SQLException;
import java.util.UUID;

public class BedAssignmentAdvice implements AfterReturningAdvice {

    private static final String TEMPLATE = "/openmrs/ws/rest/v1/bedPatientAssignment/%s?v=custom:(uuid,startDatetime,endDatetime,bed,patient,encounter:(uuid,encounterDatetime,encounterType:(uuid,name),visit:(uuid,startDatetime,visitType)))";
    public static final String CATEGORY = "encounter";
    public static final String TITLE = "Bed-Assignment";
    private static final String ASSIGN_BED_METHOD = "assignPatientToBed";
    private static final String UNASSIGN_BED_METHOD = "unAssignPatientFromBed";

    private AtomFeedSpringTransactionManager atomFeedSpringTransactionManager;

    private EventService eventService;

    public BedAssignmentAdvice() throws SQLException {
        atomFeedSpringTransactionManager = new AtomFeedSpringTransactionManager(getSpringPlatformTransactionManager());
        AllEventRecordsJdbcImpl records = new AllEventRecordsJdbcImpl(atomFeedSpringTransactionManager);
        this.eventService = new EventServiceImpl(records);
    }

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        String execMethodName = method.getName();
        if (execMethodName.equals(ASSIGN_BED_METHOD)) {
            BedDetails bedDetails = (BedDetails) returnValue;
            publishEvent(bedDetails.getLastAssignment());
            publishEvent(bedDetails.getCurrentAssignment());
        } else if (execMethodName.equals(UNASSIGN_BED_METHOD)) {
            BedDetails bedDetails = (BedDetails) returnValue;
            publishEvent(bedDetails.getLastAssignment());
        }
    }

    private Object publishEvent(final BedPatientAssignment assignment) {
        return atomFeedSpringTransactionManager.executeWithTransaction(
                new AFTransactionWorkWithoutResult() {
                    @Override
                    protected void doInTransaction() {
                        eventService.notify(getBedAssignmentEvent(assignment));
                    }
                    @Override
                    public PropagationDefinition getTxPropagationDefinition() {
                        return PropagationDefinition.PROPAGATION_REQUIRED;
                    }
                }
        );
    }

    private Event getBedAssignmentEvent(BedPatientAssignment assignment) {
        if (assignment != null) {
            String contents = String.format(TEMPLATE, assignment.getUuid());
            return new Event(UUID.randomUUID().toString(), TITLE, DateTime.now(), (URI) null, contents, CATEGORY);
        }
        return null;
    }

    private PlatformTransactionManager getSpringPlatformTransactionManager() {
        return Context.getRegisteredComponents(PlatformTransactionManager.class).get(0);
    }
}
