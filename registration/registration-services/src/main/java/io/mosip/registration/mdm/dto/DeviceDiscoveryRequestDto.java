package io.mosip.registration.mdm.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Holds the request data for the device discovery
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Getter
@Setter
public class DeviceDiscoveryRequestDto {

	/* type - “Fingerprint” “Face”, ,”Iris”, “Vein”  */
	private String type;
}
