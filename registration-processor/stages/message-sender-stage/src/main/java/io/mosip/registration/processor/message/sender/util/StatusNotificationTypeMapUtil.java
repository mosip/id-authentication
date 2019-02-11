package io.mosip.registration.processor.message.sender.util;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.registration.processor.message.sender.utility.NotificationTemplateType;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;

/**
 * The Class StatusNotificationTypeMapUtil.
 * 
 * @author M1048358 Alok
 */
@Component
public class StatusNotificationTypeMapUtil {

	/** The status map. */
	private static EnumMap<RegistrationStatusCode, NotificationTemplateType> statusMap = new EnumMap<>(
			RegistrationStatusCode.class);

	/** The unmodifiable map. */
	private static Map<RegistrationStatusCode, NotificationTemplateType> unmodifiableMap = Collections
			.unmodifiableMap(statusMap);

	/**
	 * Instantiates a new registration status map util.
	 */
	public StatusNotificationTypeMapUtil() {
		super();
	}

	/**
	 * Status mapper.
	 *
	 * @return the map
	 */
	private static Map<RegistrationStatusCode, NotificationTemplateType> statusMapper() {

		statusMap.put(RegistrationStatusCode.PACKET_OSI_VALIDATION_FAILED, NotificationTemplateType.TECHNICAL_ISSUE);
		statusMap.put(RegistrationStatusCode.STRUCTURE_VALIDATION_FAILED, NotificationTemplateType.TECHNICAL_ISSUE);
		statusMap.put(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED, NotificationTemplateType.DUPLICATE_UIN);
		statusMap.put(RegistrationStatusCode.PACKET_DEMO_DEDUPE_FAILED, NotificationTemplateType.DUPLICATE_UIN);
		statusMap.put(RegistrationStatusCode.UIN_GENERATED, NotificationTemplateType.UIN_CREATED);
		statusMap.put(RegistrationStatusCode.PACKET_UIN_UPDATION_SUCCESS, NotificationTemplateType.UIN_UPDATE);

		return unmodifiableMap;
	}

	/**
	 * Gets the template type.
	 *
	 * @param code the code
	 * @return the template type
	 */
	public NotificationTemplateType getTemplateType(String code) {
		NotificationTemplateType type = null;
		Map<RegistrationStatusCode, NotificationTemplateType> map = StatusNotificationTypeMapUtil.statusMapper();
		type = map.get(RegistrationStatusCode.valueOf(code));

		return type;
	}

}
