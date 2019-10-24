package io.mosip.registration.dto.json.metadata;

import lombok.Data;

@Data
public class RegisteredDevice {

	private String deviceCode;
	private String deviceServiceVersion;
	private DigitalId digitalId;
}
