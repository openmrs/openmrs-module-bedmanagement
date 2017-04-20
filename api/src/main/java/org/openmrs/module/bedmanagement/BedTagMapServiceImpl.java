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
package org.openmrs.module.bedmanagement;


import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import java.util.Date;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;


public class BedTagMapServiceImpl extends BaseOpenmrsService implements BedTagMapService {

    BedTagMapDAO dao;

    public void setDao(BedTagMapDAO dao) {
        this.dao = dao;
    }

    @Override
    public BedTagMap save(BedTagMap bedTagMap) throws IllegalPropertyException{
        if (getBedTagMapWithBedAndTag(bedTagMap.getBed(), bedTagMap.getBedTag()) != null) {
            throw new IllegalPropertyException("Tag Already Present For Bed");
        }
        return dao.saveOrUpdate(bedTagMap);
    }

    @Override
    public void delete(BedTagMap bedTagMap, String reason) {
        bedTagMap.setVoided(true);
        bedTagMap.setDateVoided(new Date());
        bedTagMap.setVoidReason(reason);
        bedTagMap.setVoidedBy(Context.getAuthenticatedUser());
        dao.saveOrUpdate(bedTagMap);
    }

    @Override
    public BedTagMap getBedTagMapByUuid(String bedTagMapUuid) {
        return dao.getBedTagMapByUuid(bedTagMapUuid);
    }

    @Override
    public BedTagMap getBedTagMapWithBedAndTag(Bed bed, BedTag bedTag) {
        return dao.getBedTagMapWithBedAndTag(bed, bedTag);
    }

    @Override
    public BedTag getBedTagByUuid(String bedTagUuid) {
        return dao.getBedTagByUuid(bedTagUuid);
    }
}
