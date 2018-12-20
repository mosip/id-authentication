package io.mosip.authentication.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BioInfo class gives biometric information as finger,face or iris.
 *
 * @author Rakesh Roshan
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BioInfo {

	/** Obtain biometric information like face,iris or finger*/
	private String bioType;
	
	/** Obtain device information like  device-id, manufacturer, model of device*/
	private DeviceInfo deviceInfo;
	
	
}
