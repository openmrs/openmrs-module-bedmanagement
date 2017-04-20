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

import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.AdmissionLocation;
import org.openmrs.module.bedmanagement.BedLayout;
import org.openmrs.module.bedmanagement.BedManagementService;
import org.openmrs.module.bedmanagement.BedTagMap;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Resource(name = RestConstants.VERSION_1 + "/admissionLocation", supportedClass = AdmissionLocation.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"})
public class AdmissionLocationResource extends DelegatingCrudResource<AdmissionLocation> {

    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
        BedManagementService bedManagementService = (BedManagementService) Context.getModuleOpenmrsServices(BedManagementService.class.getName()).get(0);
        List<AdmissionLocation> admissionLocations = bedManagementService.getAllAdmissionLocations();
        return new AlreadyPaged<AdmissionLocation>(context, admissionLocations, false);
    }

    @Override
    public List<Representation> getAvailableRepresentations() {
        return Arrays.asList(Representation.DEFAULT, Representation.FULL);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if ((rep instanceof DefaultRepresentation) || (rep instanceof RefRepresentation)) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("ward");
            description.addProperty("totalBeds");
            description.addProperty("occupiedBeds");
            return description;
        }
        if ((rep instanceof FullRepresentation)) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("bedLayouts");
            return description;
        }
        return null;
    }

    @PropertyGetter("bedLayouts")
    public Object getBedLayouts(AdmissionLocation admissionLocation) throws Exception {
        List<BedLayout> bedLayouts = admissionLocation.getBedLayouts();
        List<SimpleObject> ret = new ArrayList<SimpleObject>();
        for (BedLayout bedLayout : bedLayouts) {
            ret.add(getBedLayout(bedLayout));
        }
        return ret;
    }

    private SimpleObject getBedLayout(BedLayout bedLayout) throws Exception {
        SimpleObject ret = new SimpleObject();
        ret.put("rowNumber", bedLayout.getRowNumber());
        ret.put("columnNumber", bedLayout.getColumnNumber());
        ret.put("bedNumber", bedLayout.getBedNumber());
        ret.put("bedId", bedLayout.getBedId());
        ret.put("status", bedLayout.getStatus());
        ret.put("bedType", bedLayout.getBedType());
        ret.put("location", bedLayout.getLocation());
        ret.put("bedTagMaps", getCustomRepresentationForBedTagMaps(bedLayout));
        ret.put("patient", getCustomRepresentationForPatient(bedLayout));
        return ret;
    }

    private List<SimpleObject> getCustomRepresentationForBedTagMaps(BedLayout bedLayout) {
        Set<BedTagMap> bedTagMaps = bedLayout.getBedTagMaps();
        String specification = "(uuid,bedTag:(name))";
        Representation rep = new CustomRepresentation(specification);
        List<SimpleObject> customBedTagMaps = new ArrayList<SimpleObject>();
        for (BedTagMap bedTagMap : bedTagMaps) {
            if (!bedTagMap.isVoided()) {
                customBedTagMaps.add((SimpleObject) ConversionUtil.convertToRepresentation(bedTagMap, rep));
            }
        }
        return customBedTagMaps;
    }

    private Object getCustomRepresentationForPatient(BedLayout bedLayout) {
        String specification = "(uuid,person:(gender,age,preferredName:(givenName,familyName),preferredAddress:default),identifiers:(identifier))";
        Representation rep = new CustomRepresentation(specification);
        return ConversionUtil.convertToRepresentation(bedLayout.getPatient(), rep);
    }

    @Override
    public AdmissionLocation getByUniqueId(String uuid) {
        BedManagementService bedManagementService = (BedManagementService) Context.getModuleOpenmrsServices(BedManagementService.class.getName()).get(0);
        LocationService locationService = Context.getLocationService();
        return bedManagementService.getLayoutForWard(locationService.getLocationByUuid(uuid));
    }

    @Override
    protected void delete(AdmissionLocation admissionLocation, String s, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("delete of admission location not supported");
    }

    @Override
    public void purge(AdmissionLocation admissionLocation, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("purge of admission location not supported");
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        throw new ResourceDoesNotSupportOperationException("create of admission location not supported");
    }

    @Override
    public AdmissionLocation newDelegate() {
        return new AdmissionLocation();
    }

    @Override
    public AdmissionLocation save(AdmissionLocation admissionLocation) {
        throw new ResourceDoesNotSupportOperationException("save of admission location not supported");
    }
}
