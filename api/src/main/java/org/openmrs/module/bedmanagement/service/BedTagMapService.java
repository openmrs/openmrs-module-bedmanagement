package org.openmrs.module.bedmanagement.service;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.entity.BedTagMap;

public interface BedTagMapService extends OpenmrsService {
	
	@Authorized(value = { "Edit Bed Tags", "Get Bed Tags", "Get Beds" }, requireAll = true)
	BedTagMap save(BedTagMap bedTagMap);
	
	@Authorized(value = { "Edit Bed Tags", "Get Bed Tags", "Get Beds" }, requireAll = true)
	void delete(BedTagMap bedTagMap, String reason);
	
	@Authorized(value = { "Get Bed Tags", "Get Beds" }, requireAll = true)
	BedTagMap getBedTagMapByUuid(String bedTagMapUuid);
	
	@Authorized(value = { "Get Bed Tags", "Get Beds" }, requireAll = true)
	BedTagMap getBedTagMapWithBedAndTag(Bed bed, BedTag bedTag);
	
	@Authorized(value = { "Get Bed Tags", "Get Beds" }, requireAll = true)
	BedTag getBedTagByUuid(String bedTagUuid);
}
