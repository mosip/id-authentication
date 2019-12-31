package io.mosip.resident.dto;

import lombok.Data;

@Data
public class OtpAuthRequestDTO {

	private String otp;
	
	private String timestamp;

}
