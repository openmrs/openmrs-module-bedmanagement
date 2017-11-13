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

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.constants.BedManagementApiConstants;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.entity.BedTagMap;
import org.openmrs.module.bedmanagement.AdmissionLocation;
import org.openmrs.module.bedmanagement.BedLayout;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.*;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.*;

@Resource(name = RestConstants.VERSION_1 + "/admissionLocation", supportedClass = AdmissionLocation.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"})
public class AdmissionLocationResource extends DelegatingCrudResource<AdmissionLocation> {

    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
        List<AdmissionLocation> admissionLocations = Context.getService(BedManagementService.class).getAdmissionLocations();
        return new AlreadyPaged<AdmissionLocation>(context, admissionLocations, false);
    }

    @Override
    public List<Representation> getAvailableRepresentations() {
        CustomRepresentation layoutRepresentation = new CustomRepresentation("layout");
        return Arrays.asList(Representation.DEFAULT, Representation.FULL, layoutRepresentation);
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
            description.addProperty("ward");
            description.addProperty("totalBeds");
            description.addProperty("occupiedBeds");
            description.addProperty("bedLayouts");
            return description;
        }
        if ((rep instanceof NamedRepresentation) && rep.getRepresentation().equals("layout")){
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("ward");
            description.addProperty("bedLocationMappings");
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

    @PropertyGetter("bedLocationMappings")
    public Object getBedLocationMappings(AdmissionLocation admissionLocation) throws Exception {
        List<BedLocationMapping> bedLocationMappings = Context.getService(BedManagementService.class)
                .getBedLocationMappingByLocation(admissionLocation.getWard());

        List<SimpleObject> ret = new ArrayList<SimpleObject>();
        for (BedLocationMapping bedLocationMapping : bedLocationMappings) {
            SimpleObject object = new SimpleObject();
            object.put("rowNumber", bedLocationMapping.getRow());
            object.put("columnNumber", bedLocationMapping.getColumn());
            object.put("bedNumber", bedLocationMapping.getBed() != null ? bedLocationMapping.getBed().getBedNumber() : null);
            object.put("bedId", bedLocationMapping.getBed() != null ? bedLocationMapping.getBed().getId() : null);
            object.put("bedUuid", bedLocationMapping.getBed() != null ? bedLocationMapping.getBed().getUuid() : null);
            object.put("status", bedLocationMapping.getBed() != null ? bedLocationMapping.getBed().getStatus() : null);
            object.put("bedType", bedLocationMapping.getBed() != null ? bedLocationMapping.getBed().getBedType() : null);
            ret.add(object);
        }
        return ret;
    }

    private SimpleObject getBedLayout(BedLayout bedLayout) throws Exception {
        SimpleObject ret = new SimpleObject();
        ret.put("rowNumber", bedLayout.getRowNumber());
        ret.put("columnNumber", bedLayout.getColumnNumber());
        ret.put("bedNumber", bedLayout.getBedNumber());
        ret.put("bedId", bedLayout.getBedId());
        ret.put("bedUuid", bedLayout.getBedUuid());
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
        if(bedTagMaps != null)
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
        Location location = Context.getService(LocationService.class).getLocationByUuid(uuid);
        return Context.getService(BedManagementService.class).getAdmissionLocationByLocation(location);
    }

    @Override
    public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
        if (propertiesToCreate.get("name") == null)
            throw new ConversionException("The name property is missing");

        Location location = new Location();
        AdmissionLocation admissionLocation = new AdmissionLocation();
        admissionLocation.setWard(location);
        admissionLocation.getWard().setName((String) propertiesToCreate.get("name"));
        if (propertiesToCreate.get("description") != null)
            admissionLocation.getWard().setDescription((String) propertiesToCreate.get("description"));
        if (propertiesToCreate.get("parentLocationUuid") != null) {
            Location parentLocation = Context.getService(LocationService.class)
                    .getLocationByUuid((String) propertiesToCreate.get("parentLocationUuid"));
            if (parentLocation == null)
                throw new IllegalPropertyException("Parent location not exist");
            admissionLocation.getWard().setParentLocation(parentLocation);
        }

        LocationTag admissionLocationTag = Context.getService(LocationService.class)
                .getLocationTagByName(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION);
        Set<LocationTag> locationTagSet = new HashSet<LocationTag>();
        locationTagSet.add(admissionLocationTag);
        admissionLocation.getWard().setTags(locationTagSet);

        Context.getService(BedManagementService.class).saveAdmissionLocation(admissionLocation);
        return ConversionUtil.convertToRepresentation(admissionLocation, context.getRepresentation());
    }

    @Override
    public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
        Location location = Context.getService(LocationService.class).getLocationByUuid(uuid);
        if (location == null)
            throw new IllegalPropertyException("Location not exist");

        AdmissionLocation admissionLocation = Context.getService(BedManagementService.class)
                .getAdmissionLocationByLocation(location);

        if (propertiesToUpdate.get("name") != null)
            admissionLocation.getWard().setName((String) propertiesToUpdate.get("name"));

        if (propertiesToUpdate.get("description") != null)
            admissionLocation.getWard().setDescription((String) propertiesToUpdate.get("description"));

        if (propertiesToUpdate.get("parentLocationUuid") != null) {
            Location parentLocation = Context.getService(LocationService.class)
                    .getLocationByUuid((String) propertiesToUpdate.get("parentLocationUuid"));
            if (parentLocation == null)
                throw new IllegalPropertyException("Parent location not exist");
            admissionLocation.getWard().setParentLocation(parentLocation);
        }

        if(propertiesToUpdate.get("bedLayout") != null){
            HashMap<String, Integer> bedLayout = propertiesToUpdate.get("bedLayout");
            Integer row = bedLayout.get("row");
            Integer column = bedLayout.get("column");
            Context.getService(BedManagementService.class).setBedLayoutForAdmissionLocation(admissionLocation, row, column);
        }

        Context.getService(BedManagementService.class).saveAdmissionLocation(admissionLocation);
        return ConversionUtil.convertToRepresentation(admissionLocation, context.getRepresentation());
    }

    @Override
    protected void delete(AdmissionLocation admissionLocation, String reason, RequestContext requestContext) throws ResponseException {
        admissionLocation.getWard().setRetired(true);
        admissionLocation.getWard().setRetireReason(reason);
        admissionLocation.getWard().setRetiredBy(Context.getAuthenticatedUser());
        admissionLocation.getWard().setDateRetired(new Date());
        Context.getService(LocationService.class).saveLocation(admissionLocation.getWard());
    }

    @Override
    public void purge(AdmissionLocation admissionLocation, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("purge of admission location not supported");
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("name");
        description.addProperty("description");
        description.addProperty("parentLocationUuid");
        return description;
    }

    @Override
    public AdmissionLocation newDelegate() {
        return new AdmissionLocation();
    }

    @Override
    public AdmissionLocation save(AdmissionLocation admissionLocation) {
        return Context.getService(BedManagementService.class).saveAdmissionLocation(admissionLocation);
    }
}
