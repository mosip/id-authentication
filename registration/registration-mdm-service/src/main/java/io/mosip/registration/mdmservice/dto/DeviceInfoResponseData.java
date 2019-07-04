package io.mosip.registration.mdmservice.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Holds the dEvice details response from the MDM service
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Getter
@Setter
public class DeviceInfoResponseData {

	private String type;
	private String subType;
	private String status;
	private DeviceInfo deviceInfo;
	private String deviceInfoSignature;
	private String serviceVersion;
	private String callbackId;
	private String deviceSubId;

}
