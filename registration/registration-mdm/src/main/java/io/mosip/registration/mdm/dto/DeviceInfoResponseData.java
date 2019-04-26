package io.mosip.registration.mdm.dto;

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

	/* type - “Fingerprint” “Face”, ,”Iris”, “Vein”  */
	private String type;
	/*
	 * subType-(Fingerprint-“Slab”,“Single”,“Touchless”) (Iris-“Single”,“Double”)
	 */
	private String subType;
	private String status;
	private DeviceInfo deviceInfo;
	private String deviceInfoSignature;
	private String serviceVersion;
	private String callbackId;
	private String deviceSubId;

}
