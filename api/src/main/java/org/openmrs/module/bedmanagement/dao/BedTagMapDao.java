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

    /**
     * Save / Update bed tag map
     *
     * @param bedTagMap {@link BedTagMap}
     * @return {@link BedTagMap}
     */
    BedTagMap saveBedTagMap(BedTagMap bedTagMap);

    /**
     * Get bed tag map by Uuid
     *
     * @param uuid {@link String} bed tag map uuid
     * @return {@link BedTagMap}
     */
    BedTagMap getBedTagMapByUuid(String uuid);

    /**
     * Get Bed tag by bed tag uuid
     *
     * @param bedTagUuid  {@link String} bed tag uuid
     * @return {@link BedTag}
     */
    BedTag getBedTagByUuid(String bedTagUuid);

    /**
     * Get Bed tag map By Bed and Bed tag
     *
     * @param bed {@link Bed}
     * @param bedTag {@link BedTag}
     * @return {@link BedTagMap}
     */
    BedTagMap getBedTagMapWithBedAndTag(Bed bed, BedTag bedTag);
}
