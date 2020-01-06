package io.mosip.registration.mdm.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaptureResponseBioDto {

	@JsonIgnore
	private CaptureResponsBioDataDto captureResponseData;

	@JsonProperty("data")
	private String captureBioData;
	private String hash;
	private String sessionKey;
	private String signature;

}
