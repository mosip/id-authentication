package io.mosip.registration.mdm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MosipBioCaptureResponse {

	@JsonProperty("data")
	private CaptureResponseData captureResponseData;

	private String hash;
	private String sessionKey;
	private String signature;

}
