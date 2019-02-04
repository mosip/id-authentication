package io.mosip.kernel.otpnotification.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class OtpNotificationRequestDto {

	@NotEmpty
	private List<String> notificationTypes;

	private String mobileNumber;

	private String emailId;

	private String smsTemplate;

	private String emailBodyTemplate;

	private String emailSubjectTemplate;

}
