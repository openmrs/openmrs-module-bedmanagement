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
package org.openmrs.module.bedmanagement.dao;

import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.entity.BedTagMap;

public interface BedTagMapDao {

	BedTagMap saveOrUpdate(BedTagMap bedTagMap);

	BedTagMap getBedTagMapByUuid(String bedTagMapUuid);

	BedTag getBedTagByUuid(String bedTagUuid);

	BedTagMap getBedTagMapWithBedAndTag(Bed bed, BedTag bedTag);

    BedTag getBedTagByName(String bedTagName);
}
