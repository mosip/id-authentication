package io.mosip.authentication.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DeviceInfo class gives device information as device-id, make, model number.
 *
 * @author Rakesh Roshan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {

	/** Device ID */
	private String deviceId;
	
	/** Device manufacturer */
	private String make;
	
	/** Device model number */
	private String model;
	
}
