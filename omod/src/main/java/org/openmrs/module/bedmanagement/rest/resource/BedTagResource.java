/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.bedmanagement.rest.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.BedManagementService;
import org.openmrs.module.bedmanagement.BedTag;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/bedTag", supportedClass = BedTag.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.*"})
public class BedTagResource extends DelegatingCrudResource<BedTag> {


    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
        BedManagementService bedManagementService = (BedManagementService) Context.getModuleOpenmrsServices(BedManagementService.class.getName()).get(0);
        List<BedTag> bedTags = bedManagementService.getAllBedTags();
        return new AlreadyPaged<BedTag>(context, bedTags, false);
    }

    @Override
    public BedTag getByUniqueId(String uniqueId) {
        return null;
    }

    @Override
    protected void delete(BedTag bedTag, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("delete not allowed on bedTag resource");
    }

    @Override
    public void purge(BedTag bedTag, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("purge not allowed on bedTag resource");
    }

    @Override
    public BedTag newDelegate() {
        return new BedTag();
    }

    @Override
    public BedTag save(BedTag bedTag) {
        throw new ResourceDoesNotSupportOperationException("save not allowed on BedTag resource");
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("id");
        description.addProperty("name");
        description.addProperty("uuid");
        return description;
    }
}
