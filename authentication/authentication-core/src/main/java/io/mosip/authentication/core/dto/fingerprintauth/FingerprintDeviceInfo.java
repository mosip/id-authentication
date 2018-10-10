package io.mosip.authentication.core.dto.fingerprintauth;

import lombok.Data;

@Data
public class FingerprintDeviceInfo {
	private String deviceId;
	private String make;
	private String model;
	private String fingetType;
}
