package org.openmrs.module.bedmanagement.service;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.entity.BedTagMap;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface BedTagMapService extends OpenmrsService{

    /**
     * Save / Update bed tag map
     * @param bedTagMap {@link BedTagMap}
     * @return {@link BedTagMap}
     * @throws IllegalPropertyException
     */
    @Authorized(value = {"Edit Tags", "Get Tags", "Get Beds"}, requireAll=true)
    BedTagMap saveBedTagMap(BedTagMap bedTagMap) throws IllegalPropertyException;

    /**
     * Soft delete bed tag map
     * @param bedTagMap {@link BedTagMap}
     * @param reason {@link String} reason of delete
     */
    @Authorized(value = {"Edit Tags", "Get Tags", "Get Beds"}, requireAll=true)
    void deleteBedTagMap(BedTagMap bedTagMap, String reason);

    /**
     * Get bed tag map by Uuid
     * @param bedTagMapUuid {@link String} uuid
     * @return {@link BedTagMap}
     */
    @Authorized(value = {"Get Tags", "Get Beds"}, requireAll=true)
    BedTagMap getBedTagMapByUuid(String bedTagMapUuid);

    /**
     * Get bed tag map by bed and bed tag
     * @param bed {@link Bed}
     * @param bedTag {@link BedTag}
     * @return {@link BedTagMap}
     */
    @Authorized(value = {"Get Tags", "Get Beds"}, requireAll=true)
    BedTagMap getBedTagMapWithBedAndTag(Bed bed, BedTag bedTag);

    /**
     * Get bed tag by bed tag uuid
     * @param bedTagUuid {@link String}
     * @return {@link BedTag}
     */
    @Authorized(value = {"Get Tags", "Get Beds"}, requireAll=true)
    BedTag getBedTagByUuid(String bedTagUuid);
}
