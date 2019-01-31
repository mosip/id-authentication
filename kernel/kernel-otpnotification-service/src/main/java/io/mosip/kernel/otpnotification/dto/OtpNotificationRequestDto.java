package io.mosip.kernel.otpnotification.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class OtpNotificationRequestDto {

	@NotEmpty
	private List<String> notificationTypes;
	@NotBlank
	private String mobileNumber;
	@NotBlank
	private String emailId;
	@NotBlank
	private String smsTemplate;
	@NotBlank
	private String emailBodyTemplate;
	@NotBlank
	private String emailSubjectTemplate;
	
}
