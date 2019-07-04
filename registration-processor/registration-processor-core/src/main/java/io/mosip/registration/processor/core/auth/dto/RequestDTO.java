package io.mosip.registration.processor.core.auth.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RequestDTO {

	private List<BioInfo> biometrics;
	
	private String otp;
	
	private String timestamp;

}
