package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class ExceptionFingerprints {
	
	private String missingFinger;
	private String exceptionDescription;
	private String exceptionType;
	
}