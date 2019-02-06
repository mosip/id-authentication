package io.mosip.preregistration.notification.service.util;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.DateUtils;

/**
 * The util class.
 * 
 * @author Sanober Noor
 * @since 1.0.0
 *
 */
@Component
public class NotificationServiceUtil {

	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/**
	 * Method to generate currentresponsetime.
	 * 
	 * @return the string.
	 */
	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}

}
