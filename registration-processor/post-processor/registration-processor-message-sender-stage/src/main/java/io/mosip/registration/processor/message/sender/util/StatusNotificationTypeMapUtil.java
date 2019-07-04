package io.mosip.registration.processor.message.sender.util;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.registration.processor.message.sender.utility.NotificationStageStatus;
import io.mosip.registration.processor.message.sender.utility.NotificationTemplateType;

/**
 * The Class StatusNotificationTypeMapUtil.
 * 
 * @author M1048358 Alok
 */
@Component
public class StatusNotificationTypeMapUtil {

	/** The status map. */
	private static EnumMap<NotificationStageStatus, NotificationTemplateType> statusMap = new EnumMap<>(
			NotificationStageStatus.class);

	/** The unmodifiable map. */
	private static Map<NotificationStageStatus, NotificationTemplateType> unmodifiableMap = Collections
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
	private static Map<NotificationStageStatus, NotificationTemplateType> statusMapper() {
		statusMap.put(NotificationStageStatus.QUALITY_CHECK_FAILED, NotificationTemplateType.TECHNICAL_ISSUE);
		statusMap.put(NotificationStageStatus.OSI_VALIDATE_FAILED, NotificationTemplateType.TECHNICAL_ISSUE);
		statusMap.put(NotificationStageStatus.VALIDATE_PACKET_FAILED, NotificationTemplateType.TECHNICAL_ISSUE);
		statusMap.put(NotificationStageStatus.MANUAL_VERIFICATION_FAILED, NotificationTemplateType.DUPLICATE_UIN);
		statusMap.put(NotificationStageStatus.UIN_GENERATOR_PROCESSED, NotificationTemplateType.UIN_CREATED);
		statusMap.put(NotificationStageStatus.BIOGRAPHIC_VERIFICATION_FAILED, NotificationTemplateType.DUPLICATE_UIN);
		statusMap.put(NotificationStageStatus.DEMOGRAPHIC_VERIFICATION_FAILED, NotificationTemplateType.DUPLICATE_UIN);

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
		Map<NotificationStageStatus, NotificationTemplateType> map = StatusNotificationTypeMapUtil.statusMapper();
		type = map.get(NotificationStageStatus.valueOf(code));
		return type;
	}

}
