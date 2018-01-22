package org.openmrs.module.bedmanagement.constants;

import org.openmrs.api.context.Context;
import java.util.Properties;

public class BedManagementProperties {
	
	private static Properties properties;
	
	final static String KEY_APP_BASE_URL = "owa.appBaseUrl";
	
	public static void initalize() {
		properties = new Properties();
		String appBaseUrl = Context.getAdministrationService().getGlobalProperty(KEY_APP_BASE_URL) != null
		        ? Context.getAdministrationService().getGlobalProperty(KEY_APP_BASE_URL)
		        : "/owa";
		properties.put("appBaseUrl", appBaseUrl);
	}
	
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
}
