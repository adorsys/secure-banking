package org.adorsys.psd2.xs2a.utils;

import org.apache.commons.lang3.StringUtils;

public class EnvProperties {
	public static String getEnvProp(String propName, boolean optional) {
		String propValue = System.getenv(propName);

		if(StringUtils.isBlank(propValue))propValue = System.getProperty(propName);
		
		if(StringUtils.isBlank(propValue)) {
			if (optional)return null;
			throw new IllegalStateException("Missing Environmen property " + propName);
		}
		return propValue;
	}

	public static String getEnvProp(String propName, String defaultValue) {
		String propValue = System.getenv(propName);
		
		if(StringUtils.isBlank(propValue))propValue = System.getProperty(propName);
		
		if(StringUtils.isBlank(propValue))return defaultValue;
		
		return propValue;
	}
}
