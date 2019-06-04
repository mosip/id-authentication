package io.mosip.registration.mdmservice.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Holds the captured biometric response from the device
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Getter
@Setter
public class MosipBioCaptureResponseDto {

	private byte[] slapImage;
	
	@JsonProperty("biometrics")
	List<MosipBioCaptureResponse> mosipBioDeviceDataResponses;

}
