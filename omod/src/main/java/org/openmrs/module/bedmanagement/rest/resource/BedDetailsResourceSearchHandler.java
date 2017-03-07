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
import org.openmrs.module.bedmanagement.BedManagementService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

@Component
public class BedDetailsResourceSearchHandler implements SearchHandler {

    @Override
    public SearchConfig getSearchConfig() {
        SearchQuery searchQuery = new SearchQuery.Builder("Allows you to fetch bed details of a patient by visit uuid, even if the patient is discharged").withRequiredParameters("visitUuid").build();
        return new SearchConfig("bedDetailsFromVisit", RestConstants.VERSION_1 + "/beds", Arrays.asList("1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"), searchQuery);
    }

    @Override
    public PageableResult search(RequestContext requestContext) throws ResponseException {
        BedManagementService bedManagementService = (BedManagementService) Context.getModuleOpenmrsServices(BedManagementService.class.getName()).get(0);
        String visitUuid = requestContext.getRequest().getParameter("visitUuid");
        BedDetails bedDetails = bedManagementService.getLatestBedDetailsByVisit(visitUuid);
        AlreadyPaged<BedDetails> alreadyPaged = new AlreadyPaged<BedDetails>(requestContext, Collections.singletonList(bedDetails), false);
        return bedDetails == null ? new EmptySearchResult() : alreadyPaged;
    }

}
