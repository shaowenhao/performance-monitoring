package com.siemens.devops.monitoring.utils;

import java.util.Map;

public class CasePropertyUtils {

	public static String getTypeInDescription(Map<String, String> caseParamMaps) {
		return getValueInDescription(caseParamMaps, "type");
	}

	public static String getEnvInDescription(Map<String, String> caseParamMaps) {
		return getValueInDescription(caseParamMaps, "env");
	}

	public static String getEnabledInDescription(Map<String, String> caseParamMaps) {
		return getValueInDescription(caseParamMaps, "enabled");
	}

	public static String getServicesInDescription(Map<String, String> caseParamMaps) {
		return getValueInDescription(caseParamMaps, "services");
	}

	public static boolean isRun(Map<String, String> caseParamMaps, String envName) {
		boolean matchEnv = false;
		boolean enabled = false;
		String env = getEnvInDescription(caseParamMaps);
		String[] values = env.split(",");
		for (String value : values) {
			if (value.trim().equalsIgnoreCase(envName)) {
				matchEnv = true;
				break;
			}
		}

		enabled = Boolean.valueOf(getEnabledInDescription(caseParamMaps));

		if (matchEnv && enabled) {
			return true;
		} else {
			return false;
		}

	}

	public static String getCaseId(Map<String, String> caseParamMaps) {
		return caseParamMaps.get("caseId").trim();
	}

	public static String getDescription(Map<String, String> caseParamMaps) {
		return caseParamMaps.get("description").trim();
	}

	private static String getValueInDescription(Map<String, String> caseParamMaps, String key) {
		String description = getDescription(caseParamMaps);
		if (description != null && !description.isEmpty()) {
			String[] parts = description.split("\\|");
			for (String part : parts) {
				if (part.contains("=")) {
					String[] keyValue = part.split("=");
					if (keyValue.length >= 2) {
						String keyName = keyValue[0].trim();
						if (keyName.equalsIgnoreCase(key)) {
							return keyValue[1];
						}
					}
				}
			}
		}
		return "";
	}
}
