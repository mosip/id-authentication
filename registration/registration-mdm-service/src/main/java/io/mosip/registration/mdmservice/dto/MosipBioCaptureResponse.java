package io.mosip.registration.mdmservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MosipBioCaptureResponse {

	@JsonProperty("data")
	private String captureBioData;

	private String hash;
	private String sessionKey;
	private String signature;

}
