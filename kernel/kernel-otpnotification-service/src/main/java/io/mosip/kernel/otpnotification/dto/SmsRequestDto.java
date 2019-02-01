package io.mosip.kernel.otpnotification.dto;

import lombok.Data;

@Data
public class SmsRequestDto {
	private String number;
	private String message;
}
