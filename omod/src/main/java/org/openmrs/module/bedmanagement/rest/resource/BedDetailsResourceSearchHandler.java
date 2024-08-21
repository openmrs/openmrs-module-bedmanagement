/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.bedmanagement.rest.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.BedDetails;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class BedDetailsResourceSearchHandler implements SearchHandler {
	
	@Override
	public SearchConfig getSearchConfig() {
		SearchQuery searchQuery = new SearchQuery.Builder(
		        "Allows you to fetch bed details of a patient by visit uuid, even if the patient is discharged")
		                .withRequiredParameters("visitUuid").build();
		return new SearchConfig("bedDetailsFromVisit", RestConstants.VERSION_1 + "/beds", Arrays.asList("1.10.* - 9.*"),
		        searchQuery);
	}
	
	@Override
	public PageableResult search(RequestContext requestContext) throws ResponseException {
		String visitUuid = requestContext.getParameter("visitUuid");
		BedDetails bedDetails = Context.getService(BedManagementService.class).getLatestBedDetailsByVisit(visitUuid);
		List<BedDetails> ret = Collections.emptyList();
		if (bedDetails != null) {
			ret = Collections.singletonList(bedDetails);
		}
		return new NeedsPaging<>(ret, requestContext);
	}
	
}
