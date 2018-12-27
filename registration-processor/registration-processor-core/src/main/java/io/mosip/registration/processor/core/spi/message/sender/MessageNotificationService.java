package io.mosip.registration.processor.core.spi.message.sender;

import java.util.Map;

/**
 * The Interface MessageNotificationService
 * 
 * @author Alok Ranjan
 */

public interface MessageNotificationService<T, U, V> {

	public T sendSmsNotification(String templateTypeCode, String id, String idType,
			Map<String, Object> attributes);

	public U sendEmailNotification(String templateTypeCode, String id,
			String idType, Map<String, Object> attributes, String[] mailCc, String subject,
			V attachment);
	
}
