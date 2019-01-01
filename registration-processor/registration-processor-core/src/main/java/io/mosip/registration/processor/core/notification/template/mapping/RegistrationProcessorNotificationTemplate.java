package io.mosip.registration.processor.core.notification.template.mapping;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class RegistrationProcessorNotificationTemplate {
	
	private String firstName;
	private String phoneNumber;
	private String emailID;
	
}
