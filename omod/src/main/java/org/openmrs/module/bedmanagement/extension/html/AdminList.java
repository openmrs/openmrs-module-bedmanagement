package org.openmrs.module.bedmanagement.extension.html;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.bedmanagement.constants.BedManagementProperties;
import org.openmrs.module.web.extension.AdministrationSectionExt;

/**
 * This class defines the links that will appear on the administration page under the
 * "bedmanagement.title" heading.
 */
public class AdminList extends AdministrationSectionExt {
	
	final String KEY_APP_BASE_URL = "owa.appBaseUrl";
	
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	public String getTitle() {
		return "bedmanagement.title";
	}
	
	/**
	 * @see AdministrationSectionExt#getLinks()
	 */
	public Map<String, String> getLinks() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		String appBaseUrl = BedManagementProperties.getProperty("appBaseUrl");
		map.put(appBaseUrl + "/bedmanagement/admissionLocations.html", "bedmanagement.admissionLocations");
		map.put(appBaseUrl + "/bedmanagement/bedTypes.html", "bedmanagement.bedTypes");
		map.put(appBaseUrl + "/bedmanagement/bedTags.html", "bedmanagement.bedTags");
		return map;
	}
	
}
