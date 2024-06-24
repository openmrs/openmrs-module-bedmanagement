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

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.StringProperty;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.AdmissionLocation;
import org.openmrs.module.bedmanagement.constants.BedManagementApiConstants;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Resource(name = RestConstants.VERSION_1
        + "/admissionLocation", supportedClass = AdmissionLocation.class, supportedOpenmrsVersions = { "1.9.* - 9.*" })

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
		if ((rep instanceof NamedRepresentation) && rep.getRepresentation().equals("layout")) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("ward");
			description.addProperty("bedLocationMappings");
			return description;
		}
		
		return null;
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl modelImpl = ((ModelImpl) super.getGETModel(rep));
		if (rep instanceof DefaultRepresentation) {
			modelImpl.property("ward", new StringProperty()).property("totalBeds", new StringProperty())
			        .property("occupiedBeds", new StringProperty());
		}
		if (rep instanceof FullRepresentation) {
			modelImpl.property("ward", new StringProperty()).property("totalBeds", new StringProperty())
			        .property("occupiedBeds", new StringProperty()).property("bedLayouts", new StringProperty());
		}
		if (rep instanceof NamedRepresentation) {
			modelImpl.property("ward", new StringProperty()).property("bedLocationMappings", new StringProperty());
		}
		
		return modelImpl;
	}
	
	@PropertyGetter("bedLocationMappings")
	public Object getBedLocationMappings(AdmissionLocation admissionLocation) throws Exception {
		List<BedLocationMapping> bedLocationMappings = Context.getService(BedManagementService.class)
		        .getBedLocationMappingsByLocation(admissionLocation.getWard());
		
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
	
	@Override
	public AdmissionLocation getByUniqueId(String uuid) {
		Location location = Context.getService(LocationService.class).getLocationByUuid(uuid);
		return Context.getService(BedManagementService.class).getAdmissionLocationByLocation(location);
	}
	
	@Override
	public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
		LocationTag admissionLocationTag = Context.getService(LocationService.class)
		        .getLocationTagByName(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION);
		if (admissionLocationTag == null) {
			throw new IllegalStateException("Server must be configured with a Location Tag named 'Admission Location'.");
		}
		
		if (propertiesToCreate.get("name") == null)
			throw new ConversionException("The name property is missing");
		
		Location location = new Location();
		location.setName((String) propertiesToCreate.get("name"));
		if (propertiesToCreate.get("description") != null)
			location.setDescription((String) propertiesToCreate.get("description"));
		if (propertiesToCreate.get("parentLocationUuid") != null) {
			Location parentLocation = Context.getService(LocationService.class)
			        .getLocationByUuid((String) propertiesToCreate.get("parentLocationUuid"));
			if (parentLocation == null)
				throw new IllegalPropertyException("Parent location not exist");
			location.setParentLocation(parentLocation);
		}
		
		Set<LocationTag> locationTagSet = new HashSet<LocationTag>();
		locationTagSet.add(admissionLocationTag);
		location.setTags(locationTagSet);
		
		AdmissionLocation admissionLocation = new AdmissionLocation();
		admissionLocation.setWard(location);
		
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
		
		if (propertiesToUpdate.get("bedLayout") != null) {
			HashMap<String, Integer> bedLayout = propertiesToUpdate.get("bedLayout");
			Integer row = bedLayout.get("row");
			Integer column = bedLayout.get("column");
			Context.getService(BedManagementService.class).setBedLayoutForAdmissionLocation(admissionLocation, row, column);
		}
		
		Context.getService(BedManagementService.class).saveAdmissionLocation(admissionLocation);
		return ConversionUtil.convertToRepresentation(admissionLocation, context.getRepresentation());
	}
	
	@Override
	protected void delete(AdmissionLocation admissionLocation, String reason, RequestContext requestContext)
	        throws ResponseException {
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
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl().property("name", new StringProperty()).property("description", new StringProperty())
		        .property("parentLocationUuid", new StringProperty());
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
