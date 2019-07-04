package io.mosip.registration.mdm.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceDiscoveryResponsetDto {

	/* type - “Fingerprint” “Face”, ,”Iris”, “Vein”  */
	private String type;
	private String deviceId;
	/*
	 * subType-(Fingerprint-“Slab”,“Single”,“Touchless”) (Iris-“Single”,“Double”)
	 */
	private String subType;
	private String deviceStatus;
	private String certification;
	private String serviceVersion;
	private String deviceSubId;
	private String callbackId;
}
