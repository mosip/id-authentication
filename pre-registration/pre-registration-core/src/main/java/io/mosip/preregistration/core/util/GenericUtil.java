package io.mosip.preregistration.core.util;

import java.util.Date;

import io.mosip.kernel.core.util.DateUtils;

public class GenericUtil {
	
	private GenericUtil() {
	}
	
	private static String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	public static String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}

}
