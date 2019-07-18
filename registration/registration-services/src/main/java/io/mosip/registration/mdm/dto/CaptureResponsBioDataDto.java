package io.mosip.registration.mdm.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Holds the captured biometric value from the device
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Getter
@Setter
public class CaptureResponsBioDataDto {

	private String deviceCode;
	private String deviceProviderID;
	private String deviceServiceID;
	private String deviceServiceVersion;
	private String bioType;
	private String bioSubType;
	private String mosipProcess;
	private String env;
	private byte[] bioValue;
	private byte[] bioExtract;
	private String registrationID;
	private String timestamp;
	private String requestedScore;
	private String qualityScore;

}
