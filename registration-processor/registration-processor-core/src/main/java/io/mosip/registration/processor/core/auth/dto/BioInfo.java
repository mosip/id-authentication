package io.mosip.registration.processor.core.auth.dto;

import lombok.Data;

@Data
public class BioInfo {

	private String data;

	private String hash;

	private String sessionKey;

	private String signature;

}
