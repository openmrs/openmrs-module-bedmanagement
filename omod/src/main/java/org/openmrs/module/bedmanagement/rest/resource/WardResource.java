package org.openmrs.module.bedmanagement.rest.resource;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.service.BedManagementService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.*;

@Resource(name = RestConstants.VERSION_1 + "/ward", order = 2, supportedClass = Location.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"})
public class WardResource extends DelegatingCrudResource<Location> {

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("locationId");
        description.addProperty("uuid");
        description.addProperty("name");
        description.addProperty("description");
        description.addProperty("rooms");
        return description;
    }

    @PropertyGetter("rooms")
    public Object getWardRooms(Location location) {
        List<Map> rooms = new ArrayList<Map>();
        for (Location subLocation : location.getChildLocations()) {
            Map<String, Object> room = new HashMap<String, Object>();
            room.put("locationId", subLocation.getLocationId());
            room.put("uuid", subLocation.getUuid());
            room.put("name", subLocation.getName());
            room.put("display", subLocation.getDisplayString());
            room.put("description", subLocation.getDescription());
            room.put("totalBed", Context.getService(BedManagementService.class).getBedCount(subLocation));
            rooms.add(room);
        }

        return rooms;
    }

    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
        List<Location> wards = Context.getService(BedManagementService.class).getAllWards();
        return new AlreadyPaged<Location>(context, wards, false);
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        String name = context.getParameter("name");
        List<Location> wards = Context.getService(BedManagementService.class).getWardsByName(name);
        return new AlreadyPaged<Location>(context, wards, false);
    }

    @Override
    public Location getByUniqueId(String uniqueId) {
        return Context.getService(BedManagementService.class).getWardByUuid(uniqueId);
    }

    @Override
    protected void delete(Location location, String reason, RequestContext context) throws ResponseException {
        location.setRetired(true);
        location.setRetireReason(reason);
        location.setRetiredBy(Context.getAuthenticatedUser());
        location.setDateRetired(new Date());
        Context.getService(LocationService.class).saveLocation(location);
    }

    @Override
    public void purge(Location delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException("purge not allowed on location resource");
    }

    @Override
    public Location newDelegate() {
        return new Location();
    }

    @Override
    public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
        if (propertiesToCreate.get("name") == null)
            throw new ConversionException("The name property is missing");

        Location ward = Context.getService(BedManagementService.class).saveWard(null, propertiesToCreate);
        return ConversionUtil.convertToRepresentation(ward, Representation.FULL);
    }

    @Override
    public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
        Location ward = Context.getService(BedManagementService.class).saveWard(uuid, propertiesToUpdate);
        return ConversionUtil.convertToRepresentation(ward, Representation.FULL);
    }

    @Override
    public Location save(Location location) {
        return Context.getService(LocationService.class).saveLocation(location);
    }
}
