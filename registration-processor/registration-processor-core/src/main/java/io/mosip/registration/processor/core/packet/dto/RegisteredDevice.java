package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class RegisteredDevice {

	private String deviceCode;
	private String deviceServiceVersion;
	private DigitalId digitalId;
}
