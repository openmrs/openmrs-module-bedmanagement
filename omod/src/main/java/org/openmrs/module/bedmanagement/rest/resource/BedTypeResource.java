package org.openmrs.module.bedmanagement.rest.resource;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.StringProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.module.bedmanagement.service.BedManagementService;
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
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/bedtype", supportedClass = BedType.class, supportedOpenmrsVersions = { "1.9.* - 9.*" })
public class BedTypeResource extends DelegatingCrudResource<BedType> {
	
	@Override
	public BedType newDelegate() {
		return new BedType();
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("name");
		description.addProperty("displayName");
		description.addProperty("description");
		return description;
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl modelImpl = ((ModelImpl) super.getGETModel(rep));
		modelImpl.property("uuid", new StringProperty()).property("name", new StringProperty())
		        .property("displayName", new StringProperty()).property("description", new StringProperty());
		return modelImpl;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = new DelegatingResourceDescription();
		delegatingResourceDescription.addRequiredProperty("name");
		delegatingResourceDescription.addRequiredProperty("displayName");
		delegatingResourceDescription.addRequiredProperty("description");
		return delegatingResourceDescription;
	}
	
	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl().property("name", new StringProperty()).property("displayName", new StringProperty())
		        .property("description", new StringProperty());
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		List<BedType> bedTypeList = Context.getService(BedManagementService.class).getBedTypes(null, context.getLimit(),
		    context.getStartIndex());
		return new AlreadyPaged<BedType>(context, bedTypeList, false);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String name = context.getParameter("name");
		List<BedType> bedTypeList = Context.getService(BedManagementService.class).getBedTypes(name, context.getLimit(),
		    context.getStartIndex());
		return new AlreadyPaged<BedType>(context, bedTypeList, false);
	}
	
	@Override
	public BedType getByUniqueId(String uuid) {
		return Context.getService(BedManagementService.class).getBedTypeByUuid(uuid);
	}
	
	@Override
	public BedType save(BedType bedType) {
		return Context.getService(BedManagementService.class).saveBedType(bedType);
	}
	
	@Override
	public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
		if (propertiesToCreate.get("name") == null || propertiesToCreate.get("displayName") == null)
			throw new ConversionException("Required properties: name, displayName");
		
		BedType bedType = this.constructBedType(null, propertiesToCreate);
		Context.getService(BedManagementService.class).saveBedType(bedType);
		return ConversionUtil.convertToRepresentation(bedType, context.getRepresentation());
	}
	
	@Override
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
		BedType bedType = this.constructBedType(uuid, propertiesToUpdate);
		Context.getService(BedManagementService.class).saveBedType(bedType);
		return ConversionUtil.convertToRepresentation(bedType, context.getRepresentation());
	}
	
	@Override
	protected void delete(BedType bedType, String reason, RequestContext context) throws ResponseException {
		Context.getService(BedManagementService.class).deleteBedType(bedType);
	}
	
	@Override
	public void purge(BedType bedType, RequestContext context) throws ResponseException {
		Context.getService(BedManagementService.class).deleteBedType(bedType);
	}
	
	private BedType constructBedType(String uuid, SimpleObject properties) {
		BedType bedType;
		if (uuid != null) {
			bedType = Context.getService(BedManagementService.class).getBedTypeByUuid(uuid);
			if (bedType == null)
				throw new IllegalPropertyException("Bed Type not exist");
			
			if (properties.get("name") != null)
				bedType.setName((String) properties.get("name"));
			
			if (properties.get("displayName") != null)
				bedType.setDisplayName((String) properties.get("displayName"));
			
			if (properties.get("description") != null)
				bedType.setDescription((String) properties.get("description"));
		} else {
			bedType = new BedType();
			if (properties.get("name") == null || properties.get("displayName") == null)
				throw new IllegalPropertyException("Required parameters: name, displayName");
			bedType.setName((String) properties.get("name"));
			bedType.setDisplayName((String) properties.get("displayName"));
			bedType.setDescription((String) properties.get("description"));
		}
		
		return bedType;
	}
}
