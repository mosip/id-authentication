package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class ExceptionIris {

	private String biometricType;
	private String missingBiometric;
	private String exceptionDescription;
	private String exceptionType;

}
