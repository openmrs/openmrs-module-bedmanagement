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
import org.openmrs.module.bedmanagement.BedPatientAssignment;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Arrays;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/bedPatientAssignment", supportedClass = BedPatientAssignment.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"})
public class BedPatientAssignmentResource extends DelegatingCrudResource<BedPatientAssignment> {

    @Override
    public List<Representation> getAvailableRepresentations() {
        return Arrays.asList(Representation.DEFAULT, Representation.FULL);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if ((rep instanceof DefaultRepresentation) || (rep instanceof RefRepresentation)) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("bed", Representation.DEFAULT);
            description.addProperty("patient", Representation.REF);
            description.addProperty("encounter", Representation.REF);
            return description;
        }

        if ((rep instanceof FullRepresentation)) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("bed", Representation.DEFAULT);
            description.addProperty("patient", Representation.REF);
            description.addProperty("encounter", Representation.FULL);
            return description;
        }
        return null;

    }

    @Override
    public BedPatientAssignment getByUniqueId(String uuid) {
        BedManagementService bedManagementService = (BedManagementService) Context.getModuleOpenmrsServices(BedManagementService.class.getName()).get(0);
        return bedManagementService.getBedPatientAssignmentByUuid(uuid);
    }

    @Override
    protected void delete(BedPatientAssignment bedPatientAssignment, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("delete of bed patient assignment not supported");
    }

    @Override
    public void purge(BedPatientAssignment bedPatientAssignment, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("purge of bed patient assignment not supported");
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        throw new ResourceDoesNotSupportOperationException("create of bed patient assignment not supported");
    }

    @Override
    public BedPatientAssignment newDelegate() {
        return new BedPatientAssignment();
    }

    @Override
    public BedPatientAssignment save(BedPatientAssignment bedPatientAssignment) {
        throw new ResourceDoesNotSupportOperationException("save of bed patient assignment not supported");
    }
}
