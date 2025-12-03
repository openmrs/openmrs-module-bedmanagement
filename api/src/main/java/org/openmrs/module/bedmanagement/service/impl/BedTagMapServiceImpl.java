/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License a
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.bedmanagement.service.impl;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.bedmanagement.dao.BedTagMapDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.entity.BedTagMap;
import org.openmrs.module.bedmanagement.service.BedTagMapService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Transactional
public class BedTagMapServiceImpl extends BaseOpenmrsService implements BedTagMapService {

	BedTagMapDao bedTagMapDao;

	public void setDao(BedTagMapDao dao) {
		this.bedTagMapDao = dao;
	}

	@Override
	@Transactional
	public BedTagMap save(BedTagMap bedTagMap) {
		if (getBedTagMapWithBedAndTag(bedTagMap.getBed(), bedTagMap.getBedTag()) != null) {
			throw new APIException("Tag Already Present For Bed");
		}
		return bedTagMapDao.saveOrUpdate(bedTagMap);
	}

	@Override
	@Transactional
	public void delete(BedTagMap bedTagMap, String reason) {
		bedTagMap.setVoided(true);
		bedTagMap.setDateVoided(new Date());
		bedTagMap.setVoidReason(reason);
		bedTagMap.setVoidedBy(Context.getAuthenticatedUser());
		bedTagMapDao.saveOrUpdate(bedTagMap);
	}

	@Override
	@Transactional(readOnly = true)
	public BedTagMap getBedTagMapByUuid(String bedTagMapUuid) {
		return bedTagMapDao.getBedTagMapByUuid(bedTagMapUuid);
	}

	@Override
	@Transactional(readOnly = true)
	public BedTagMap getBedTagMapWithBedAndTag(Bed bed, BedTag bedTag) {
		return bedTagMapDao.getBedTagMapWithBedAndTag(bed, bedTag);
	}

	@Override
	@Transactional(readOnly = true)
	public BedTag getBedTagByUuid(String bedTagUuid) {
		return bedTagMapDao.getBedTagByUuid(bedTagUuid);
	}

	@Transactional(readOnly = true)
    public BedTag getBedTagByName(String bedTagName) {
        return bedTagMapDao.getBedTagByName(bedTagName);
    }
}
