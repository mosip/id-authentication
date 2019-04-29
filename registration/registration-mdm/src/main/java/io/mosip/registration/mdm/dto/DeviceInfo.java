package io.mosip.registration.mdm.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Holds the Biometric Device details
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Getter
@Setter
public class DeviceInfo {

	private String deviceId;
	private String deviceSubId;
	private String firmware;
	private String deviceProviderName;
	private String deviceProviderId;
	private String deviceModel;
	private String deviceMake;
	private String deviceExpiry;
	private String certification;
	private String timestamp;

}
