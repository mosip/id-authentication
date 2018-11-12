package io.mosip.authentication.service.integration.dto;

import lombok.Data;

@Data
public class SmsRequestDto {

	String message;
	String number;
}
