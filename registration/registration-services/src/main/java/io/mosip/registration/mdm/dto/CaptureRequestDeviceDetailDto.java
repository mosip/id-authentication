package io.mosip.registration.mdm.dto;

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
public class CaptureRequestDeviceDetailDto {

	private String type;
	private int count;
	private String format;
	private String requestedScore;
	private String deviceId;
	private String deviceSubId;
	private String previousHash;

}
