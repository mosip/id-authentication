package io.mosip.registration.mdmservice.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Holds the Biometric capture request data
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Getter
@Setter
public class MosipBioCaptureRequestDto {

	private String env;
	private String mosipProcess;
	private String version;
	private int timeout;
	private String captureTime;
	private String transactionId;

	@JsonProperty("bio")
	private List<MosipBioRequest> mosipBioRequest;

	private Map<String, String> customOpts;
}
