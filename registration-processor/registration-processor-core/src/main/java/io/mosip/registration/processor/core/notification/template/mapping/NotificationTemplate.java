package io.mosip.registration.processor.core.notification.template.mapping;

import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import lombok.Data;

@Data
public class NotificationTemplate {
	
	private JsonValue[] firstName;

	private JsonValue[] phoneNumber;

	private JsonValue[] emailID;
	
}
