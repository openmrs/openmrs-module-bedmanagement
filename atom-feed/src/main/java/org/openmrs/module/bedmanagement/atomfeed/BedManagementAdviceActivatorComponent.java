package org.openmrs.module.bedmanagement.atomfeed;

import org.aopalliance.aop.Advice;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.BedManagementActivatorComponent;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.bedmanagement.service.BedTagMapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers AOP advice when the context starts up. This is done instead of declaring advice in
 * config.xml in order to allow it to be conditionally loaded
 */
@OpenmrsProfile(modules = { "openmrs-atomfeed:*" })
public class BedManagementAdviceActivatorComponent implements BedManagementActivatorComponent {
	
	private static final Logger log = LoggerFactory.getLogger(BedManagementAdviceActivatorComponent.class);
	
	private final BedAssignmentAdvice bedAssignmentAdvice;
	
	private final BedTagMapAdvice bedTagMapAdvice;
	
	public BedManagementAdviceActivatorComponent() {
		this(new BedAssignmentAdvice(), new BedTagMapAdvice());
	}
	
	public BedManagementAdviceActivatorComponent(BedAssignmentAdvice bedAssignmentAdvice, BedTagMapAdvice bedTagMapAdvice) {
		this.bedAssignmentAdvice = bedAssignmentAdvice;
		this.bedTagMapAdvice = bedTagMapAdvice;
	}
	
	protected void addAdvice(Class<?> advicePoint, Advice advice) {
		log.info("Adding AOP: " + advicePoint.getSimpleName() + " -> " + advice.getClass().getSimpleName());
		Context.addAdvice(advicePoint, advice);
	}
	
	protected void removeAdvice(Class<?> advicePoint, Advice advice) {
		log.info("Removing AOP: " + advicePoint.getSimpleName() + " -> " + advice.getClass().getSimpleName());
		Context.removeAdvice(advicePoint, advice);
	}
	
	@Override
	public void started() {
		addAdvice(BedManagementService.class, bedAssignmentAdvice);
		addAdvice(BedTagMapService.class, bedTagMapAdvice);
	}
	
	@Override
	public void willStop() {
		removeAdvice(BedManagementService.class, bedAssignmentAdvice);
		removeAdvice(BedTagMapService.class, bedTagMapAdvice);
	}
}
