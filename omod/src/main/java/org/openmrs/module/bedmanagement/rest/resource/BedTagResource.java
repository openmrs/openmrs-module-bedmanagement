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

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.StringProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
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

@Resource(name = RestConstants.VERSION_1 + "/bedTag", supportedClass = BedTag.class, supportedOpenmrsVersions = { "1.9.* - 9.*" })
public class BedTagResource extends DelegatingCrudResource<BedTag> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("id");
		description.addProperty("name");
		description.addProperty("uuid");
		return description;
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl modelImpl = ((ModelImpl) super.getGETModel(rep));
		modelImpl.property("id", new StringProperty()).property("name", new StringProperty()).property("uuid",
		    new StringProperty());
		
		return modelImpl;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("id");
		description.addProperty("name");
		return description;
	}
	
	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl().property("id", new StringProperty()).property("name", new StringProperty());
	}
	
	@Override
	public BedTag newDelegate() {
		return new BedTag();
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		List<BedTag> bedTags = Context.getService(BedManagementService.class).getBedTags(null, context.getLimit(),
		    context.getStartIndex());
		return new AlreadyPaged<BedTag>(context, bedTags, false);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String name = context.getParameter("name");
		List<BedTag> bedTags = Context.getService(BedManagementService.class).getBedTags(name, context.getLimit(),
		    context.getStartIndex());
		return new AlreadyPaged<BedTag>(context, bedTags, false);
	}
	
	@Override
	public BedTag getByUniqueId(String uniqueId) {
		return Context.getService(BedManagementService.class).getBedTagByUuid(uniqueId);
	}
	
	@Override
	public BedTag save(BedTag bedTag) {
		return Context.getService(BedManagementService.class).saveBedTag(bedTag);
	}
	
	@Override
	public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
		if (propertiesToCreate.get("name") == null)
			throw new ConversionException("Required properties: name");
		
		BedTag bedTag = this.constructBedTag(null, propertiesToCreate);
		Context.getService(BedManagementService.class).saveBedTag(bedTag);
		return ConversionUtil.convertToRepresentation(bedTag, context.getRepresentation());
	}
	
	@Override
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
		BedTag bedTag = this.constructBedTag(uuid, propertiesToUpdate);
		Context.getService(BedManagementService.class).saveBedTag(bedTag);
		return ConversionUtil.convertToRepresentation(bedTag, context.getRepresentation());
	}
	
	@Override
	protected void delete(BedTag bedTag, String reason, RequestContext context) throws ResponseException {
		Context.getService(BedManagementService.class).deleteBedTag(bedTag, reason);
	}
	
	@Override
	public void purge(BedTag bedTag, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("purge not allowed on bed tag resource");
	}
	
	private BedTag constructBedTag(String uuid, SimpleObject properties) {
		BedTag bedTag;
		if (uuid != null) {
			bedTag = Context.getService(BedManagementService.class).getBedTagByUuid(uuid);
			if (bedTag == null)
				throw new IllegalPropertyException("Bed Tag not exist");
			
			if (properties.get("name") != null)
				bedTag.setName((String) properties.get("name"));
		} else {
			bedTag = new BedTag();
			if (properties.get("name") == null)
				throw new IllegalPropertyException("Required parameters: name");
			bedTag.setName((String) properties.get("name"));
		}
		
		return bedTag;
	}
}
