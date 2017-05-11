package org.openmrs.module.bedmanagement;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface BedTagMapService extends OpenmrsService{

    @Authorized(value = {"Edit Tags", "Get Tags", "Get Beds"}, requireAll=true)
    BedTagMap save(BedTagMap bedTagMap) throws IllegalPropertyException;

    @Authorized(value = {"Edit Tags", "Get Tags", "Get Beds"}, requireAll=true)
    void delete(BedTagMap bedTagMap, String reason);

    @Authorized(value = {"Get Tags", "Get Beds"}, requireAll=true)
    BedTagMap getBedTagMapByUuid(String bedTagMapUuid);

    @Authorized(value = {"Get Tags", "Get Beds"}, requireAll=true)
    BedTagMap getBedTagMapWithBedAndTag(Bed bed, BedTag bedTag);

    @Authorized(value = {"Get Tags", "Get Beds"}, requireAll=true)
    BedTag getBedTagByUuid(String bedTagUuid);
}
