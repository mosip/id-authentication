package io.mosip.authentication.core.dto.fingerprintauth;

import lombok.Data;

/**
 * Class that contains all the information about the fingerprint device.
 * 
 * @author Manoj SP
 *
 */
@Data
public class FingerprintDeviceInfo {

	private String deviceId;
	private String make;
	private String model;
	private String fingerType;
}
