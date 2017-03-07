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

import org.openmrs.Encounter;
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

@Resource(name = RestConstants.VERSION_1 + "/beds", supportedClass = BedDetails.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"})
public class BedDetailsResource extends DelegatingCrudResource<BedDetails> {
    @Override
    public BedDetails getByUniqueId(String id) {
        BedManagementService bedManagementService = (BedManagementService) Context.getModuleOpenmrsServices(BedManagementService.class.getName()).get(0);
        BedDetails bedDetails = bedManagementService.getBedDetailsById(id);
        if (bedDetails == null)
            bedDetails = bedManagementService.getBedDetailsByUuid(id);
        return bedDetails;
    }

    @Override
    protected void delete(BedDetails bedDetails, String reason, RequestContext requestContext) throws ResponseException {
        String patientUuid = requestContext.getParameter("patientUuid");
        BedManagementService bedManagementService = (BedManagementService) Context.getModuleOpenmrsServices(BedManagementService.class.getName()).get(0);
        bedManagementService.unAssignPatientFromBed(Context.getPatientService().getPatientByUuid(patientUuid));
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
            description.addProperty("physicalLocation", Representation.DEFAULT);
            description.addProperty("patients", Representation.DEFAULT);
            return description;
        }
        if ((rep instanceof FullRepresentation)) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("bedId", Representation.FULL);
            description.addProperty("bedNumber", Representation.FULL);
            description.addProperty("bedType");
            description.addProperty("physicalLocation", Representation.FULL);
            description.addProperty("patients", Representation.FULL);
            return description;
        }
        return null;
    }

    @Override
    public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
        BedManagementService bedManagementService = (BedManagementService) Context.getModuleOpenmrsServices(BedManagementService.class.getName()).get(0);
        Patient patient = Context.getPatientService().getPatientByUuid((String) propertiesToUpdate.get("patientUuid"));
        Object encounterUuid = propertiesToUpdate.get("encounterUuid");
        Encounter encounter = null;
        if (encounterUuid != null) {
            encounter = Context.getEncounterService().getEncounterByUuid((String) encounterUuid);
        }
        BedDetails bedRes = bedManagementService.assignPatientToBed(patient, encounter, uuid);
        return ConversionUtil.convertToRepresentation(bedRes, Representation.DEFAULT);
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        BedManagementService bedManagementService = (BedManagementService) Context.getModuleOpenmrsServices(BedManagementService.class.getName()).get(0);
        String patientUuid = context.getParameter("patientUuid");
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        BedDetails bedDetails = bedManagementService.getBedAssignmentDetailsByPatient(patient);
        AlreadyPaged<BedDetails> alreadyPaged = new AlreadyPaged<BedDetails>(context, Arrays.asList(bedDetails), false);
        return bedDetails == null || bedDetails.getBedId() == 0 ? super.doSearch(context) : alreadyPaged;
    }
}
