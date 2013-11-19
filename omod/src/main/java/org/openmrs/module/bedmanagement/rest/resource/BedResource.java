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

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.BedDetails;
import org.openmrs.module.bedmanagement.BedManagementService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Arrays;

@Resource(name = RestConstants.VERSION_1 + "/beds", supportedClass = BedDetails.class, supportedOpenmrsVersions = "1.9.*")
public class BedResource extends DelegatingCrudResource<BedDetails> {
    @Override
    public BedDetails getByUniqueId(String id) {
        BedManagementService bedManagementService = (BedManagementService) Context.getModuleOpenmrsServices(BedManagementService.class.getName()).get(0);
        return bedManagementService.getBedDetailsById(id);
    }

    @Override
    protected void delete(BedDetails bedDetails, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("delete of bed not supported");
    }

    @Override
    public BedDetails newDelegate() {
        return new BedDetails();
    }

    @Override
    public BedDetails save(BedDetails bedDetails) {
        throw new ResourceDoesNotSupportOperationException("save of bed not supported");
    }

    @Override
    public void purge(BedDetails bedDetails, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("purge of bed not supported");
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if ((rep instanceof DefaultRepresentation) || (rep instanceof RefRepresentation)) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("bedId", "bedId");
            description.addProperty("bedNumber", "bedNumber");
            description.addProperty("bedType");
            description.addProperty("physicalLocation",Representation.DEFAULT);
            description.addProperty("patient",Representation.DEFAULT);
            return description;
        }
        if ((rep instanceof FullRepresentation)) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("bedId", Representation.FULL);
            description.addProperty("bedNumber", Representation.FULL);
            description.addProperty("bedType");
            description.addProperty("physicalLocation",Representation.FULL);
            description.addProperty("patient",Representation.FULL);
            return description;
        }
        return null;
    }

    @Override
    public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
        BedManagementService bedManagementService = (BedManagementService) Context.getModuleOpenmrsServices(BedManagementService.class.getName()).get(0);
        Patient patient = Context.getPatientService().getPatientByUuid((String) propertiesToUpdate.get("patientUuid"));
        BedDetails bedRes = bedManagementService.assignPatientToBed(patient, uuid);
        SimpleObject ret = (SimpleObject) ConversionUtil.convertToRepresentation(bedRes, Representation.DEFAULT);
        return ret;
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        BedManagementService bedManagementService = (BedManagementService) Context.getModuleOpenmrsServices(BedManagementService.class.getName()).get(0);
        String patientUuid = context.getRequest().getParameter("patientUuid");
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        BedDetails bedDetails = bedManagementService.getBedAssignmentDetailsByPatient(patient);
        AlreadyPaged<BedDetails> alreadyPaged = new AlreadyPaged<BedDetails>(context, Arrays.asList(bedDetails) ,false);
        return bedDetails == null || bedDetails.getBedId() == 0 ? super.doSearch(context) : alreadyPaged;
    }
}
