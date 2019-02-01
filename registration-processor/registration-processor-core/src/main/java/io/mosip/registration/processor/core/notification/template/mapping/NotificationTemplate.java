package io.mosip.registration.processor.core.notification.template.mapping;

import java.util.Arrays;

import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import lombok.Data;

@Data
public class NotificationTemplate {
	
	private JsonValue[] firstName;

	public JsonValue[] getFirstName() {
		return Arrays.copyOf(firstName, firstName.length);
	}

	public void setFirstName(JsonValue[] firstName) {
		this.firstName = firstName!=null?firstName:null;
	}

	private String phoneNumber;

	private String emailID;
	
}
