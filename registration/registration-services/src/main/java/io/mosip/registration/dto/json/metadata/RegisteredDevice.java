package io.mosip.registration.dto.json.metadata;

import lombok.Data;

/**
 * This class will hold the details about registered devices upon validation
 *  
 * @author Taleev.Aalam
 * @since 1.0.0
 */

@Data
public class RegisteredDevice {

	private String deviceCode;
	private String deviceServiceVersion;
	private DigitalId digitalId;
}
