package io.mosip.registration.mdm.dto;

import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CaptureRequestDto {

	private String env;
	private String mosipProcess;
	private String version;
	private int timeout;
	@JsonIgnore
	private String captureTime;
	@JsonIgnore
	private String registrationID;

	@JsonProperty("bio")
	private List<CaptureRequestDeviceDetailDto> mosipBioRequest;

	private List<Map<String, String>> customOpts;
}
