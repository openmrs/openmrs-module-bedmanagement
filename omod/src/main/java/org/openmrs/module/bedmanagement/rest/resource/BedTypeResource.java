package org.openmrs.module.bedmanagement.rest.resource;

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

@Resource(name = RestConstants.VERSION_1 + "/bedtype", supportedClass = BedType.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"})
public class BedTypeResource extends DelegatingCrudResource<BedType> {

    @Override
    public BedType newDelegate() {
        return new BedType();
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("id");
        description.addProperty("name");
        description.addProperty("displayName");
        description.addProperty("description");
        return description;
    }

    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
        List<BedType> bedTypeList = Context.getService(BedManagementService.class).getBedTypes(null, context.getLimit(), context.getStartIndex());
        return new AlreadyPaged<BedType>(context, bedTypeList, false);
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        String name = context.getParameter("name");
        List<BedType> bedTypeList = Context.getService(BedManagementService.class).getBedTypes(name, context.getLimit(), context.getStartIndex());
        return new AlreadyPaged<BedType>(context, bedTypeList, false);
    }

    @Override
    public BedType getByUniqueId(String id) {
        return Context.getService(BedManagementService.class).getBedTypeById(Integer.parseInt(id));
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
    public Object update(String id, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
        BedType bedType = this.constructBedType(Integer.parseInt(id), propertiesToUpdate);
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

    private BedType constructBedType(Integer id, SimpleObject properties){
        BedType bedType;
        if (id != null) {
            bedType = Context.getService(BedManagementService.class).getBedTypeById(id);
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
