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
import org.openmrs.module.bedmanagement.BedTagMap;
import org.openmrs.module.bedmanagement.BedTagMapService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + "/bedTagMap", supportedClass = BedTagMap.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.*"})
public class BedTagMapResource extends DataDelegatingCrudResource<BedTagMap> {

    @Override
    public BedTagMap getByUniqueId(String uniqueId) {
        BedTagMapService bedTagMapService = Context.getService(BedTagMapService.class);
        return bedTagMapService.getBedTagMapByUuid(uniqueId);
    }

    @Override
    protected void delete(BedTagMap delegate, String reason, RequestContext context) throws ResponseException {
        BedTagMapService bedTagMapService = Context.getService(BedTagMapService.class);
        bedTagMapService.delete(delegate, reason);
    }

    @Override
    public BedTagMap newDelegate() {
        return new BedTagMap();
    }

    @Override
    public BedTagMap save(BedTagMap delegate) {
        return Context.getService(BedTagMapService.class).save(delegate);
    }

    @Override
    public void purge(BedTagMap delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("purge not allowed on bedTagMap resource");
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if ((rep instanceof DefaultRepresentation) || (rep instanceof RefRepresentation)) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            return description;
        }
        return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
        delegatingResourceDescription.addRequiredProperty("bed");
        delegatingResourceDescription.addRequiredProperty("bedTag");
        return delegatingResourceDescription;
    }
}
