/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p/>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p/>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.bedmanagement.rest.resource;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/bed", supportedClass = Bed.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"})
public class BedResource extends DelegatingCrudResource<Bed> {
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if ((rep instanceof DefaultRepresentation) || (rep instanceof RefRepresentation)) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("id");
            description.addProperty("uuid");
            description.addProperty("bedNumber");
            description.addProperty("bedType");
            description.addProperty("row");
            description.addProperty("column");
            description.addProperty("status", Representation.DEFAULT);
            return description;
        }
        if ((rep instanceof FullRepresentation)) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("id");
            description.addProperty("uuid");
            description.addProperty("bedNumber");
            description.addProperty("bedType");
            description.addProperty("row");
            description.addProperty("column");
            description.addProperty("status", Representation.FULL);
            return description;
        }
        return null;
    }

    @PropertyGetter("row")
    public Integer getRow(Bed bed) {
        BedLocationMapping bedLocationMapping = Context.getService(BedManagementService.class)
                .getBedLocationMappingByBedId(bed.getId());
        return bedLocationMapping != null ? bedLocationMapping.getRow() : null;
    }

    @PropertyGetter("column")
    public Integer getColumn(Bed bed) {
        BedLocationMapping bedLocationMapping = Context.getService(BedManagementService.class)
                .getBedLocationMappingByBedId(bed.getId());
        return bedLocationMapping != null ? bedLocationMapping.getColumn() : null;
    }

    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
        List<Bed> bedList = Context.getService(BedManagementService.class)
                .getBeds(context.getLimit(), context.getStartIndex());
        return new AlreadyPaged<Bed>(context, bedList, false);
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        BedStatus bedStatus = null;
        if (context.getParameter("status") != null)
            bedStatus = context.getParameter("status").equals("AVAILABLE") ? BedStatus.AVAILABLE : BedStatus.OCCUPIED;
        String bedType = context.getParameter("bedType");
        String locationUuid = context.getParameter("locationUuid");
        List<Bed> bedList = Context.getService(BedManagementService.class).getBeds(locationUuid, bedType, bedStatus, context.getLimit(), context.getStartIndex());
        return new AlreadyPaged<Bed>(context, bedList, false);
    }

    @Override
    public Bed getByUniqueId(String uuid) {
        return Context.getService(BedManagementService.class).getBedByUuid(uuid);
    }

    @Override
    protected void delete(Bed bed, String reason, RequestContext requestContext) throws ResponseException {
        Context.getService(BedManagementService.class).deleteBed(bed, reason);
    }

    @Override
    public Bed newDelegate() {
        return new Bed();
    }

    @Override
    public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
        if (propertiesToCreate.get("bedNumber") == null || propertiesToCreate.get("bedType") == null)
            throw new ConversionException("Required parameters:  bedNumber, bedType, row, column, locationUuid");

        Bed bed = this.constructBed(null, propertiesToCreate);
        Context.getService(BedManagementService.class).saveBed(bed);
        if (propertiesToCreate.get("row") != null && propertiesToCreate.get("column") != null &&
                propertiesToCreate.get("locationUuid") != null) {
            String locationUuid = propertiesToCreate.get("locationUuid");
            Integer row = propertiesToCreate.get("row");
            Integer column = propertiesToCreate.get("column");
            BedLocationMapping bedLocationMapping = this.constructBedLocationMapping(bed, locationUuid, row, column);
            Context.getService(BedManagementService.class).saveBedLocationMapping(bedLocationMapping);
        }

        System.out.println(bed);
        return ConversionUtil.convertToRepresentation(bed, Representation.FULL);
    }

    @Override
    public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
        Bed bed = this.constructBed(uuid, propertiesToUpdate);
        Context.getService(BedManagementService.class).saveBed(bed);
        if (propertiesToUpdate.get("row") != null && propertiesToUpdate.get("column") != null &&
                propertiesToUpdate.get("locationUuid") != null) {
            String locationUuid = propertiesToUpdate.get("locationUuid");
            Integer row = propertiesToUpdate.get("row");
            Integer column = propertiesToUpdate.get("column");
            BedLocationMapping bedLocationMapping = this.constructBedLocationMapping(bed, locationUuid, row, column);
            Context.getService(BedManagementService.class).saveBedLocationMapping(bedLocationMapping);
        }

        return ConversionUtil.convertToRepresentation(bed, Representation.FULL);
    }

    @Override
    public Bed save(Bed bed) {
        return Context.getService(BedManagementService.class).saveBed(bed);
    }

    @Override
    public void purge(Bed bed, RequestContext requestContext) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("purge not allowed on bed resource");
    }

    private Bed constructBed(String uuid, SimpleObject properties) {
        Bed bed;
        BedType bedType = null;
        if (properties.get("bedType") != null) {
            String bedTypeName = properties.get("bedType");
            List<BedType> bedTypes = Context.getService(BedManagementService.class).getBedTypes(bedTypeName, 1, 0);
            if (bedTypes.size() == 0)
                throw new IllegalPropertyException("Invalid bed type name");
            bedType = bedTypes.get(0);
        }

        if (uuid != null) {
            bed = Context.getService(BedManagementService.class).getBedByUuid(uuid);
            if (bed == null)
                throw new IllegalPropertyException("Bed not exist");

            if (properties.get("bedNumber") != null)
                bed.setBedNumber((String) properties.get("bedNumber"));

            if (properties.get("status") != null)
                bed.setStatus((String) properties.get("status"));

            if (bedType != null) {
                bed.setBedType(bedType);
            }
        } else {
            bed = new Bed();
            if (properties.get("bedNumber") == null || properties.get("bedType") == null ||
                    properties.get("row") == null || properties.get("column") == null ||
                    properties.get("locationUuid") == null)
                throw new IllegalPropertyException("Required parameters: bedNumber, bedType, row, column, locationUuid");
            bed.setBedNumber((String) properties.get("bedNumber"));
            bed.setStatus(properties.get("status") != null ? (String) properties.get("status") : "AVAILABLE");
            bed.setBedType(bedType);
        }

        return bed;
    }

    private BedLocationMapping constructBedLocationMapping(Bed bed, String locationUuid, Integer row, Integer column) {
        BedLocationMapping bedLocationMapping = Context.getService(BedManagementService.class).getBedLocationMappingByBedId(bed.getId());
        if (bedLocationMapping != null)
            bedLocationMapping.setBed(null);

        BedLocationMapping existingBedLocationMapping = Context.getService(BedManagementService.class)
                .getBedLocationMappingByLocationUuidAndRowColumn(locationUuid, row, column);

        if (existingBedLocationMapping == null) {
            Location location = Context.getService(LocationService.class).getLocationByUuid(locationUuid);
            existingBedLocationMapping = new BedLocationMapping();
            existingBedLocationMapping.setLocation(location);
            existingBedLocationMapping.setRow(row);
            existingBedLocationMapping.setColumn(column);
        } else if (existingBedLocationMapping.getBed() != null) {
            throw new IllegalPropertyException("Already bed assign to give row & column");
        }

        existingBedLocationMapping.setBed(bed);
        return existingBedLocationMapping;
    }
}
