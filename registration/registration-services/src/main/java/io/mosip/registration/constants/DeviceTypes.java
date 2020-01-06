package io.mosip.registration.constants;

/**
 * Device Type enum
 * @author Dinesh Ashokan
 *
 */
public enum DeviceTypes {
	
	FINGERPRINT("Fingerprint"),
	IRIS("IRIS"),
	WEBCAM("webcam");
	
	/**
	 * @param code
	 */
	private DeviceTypes(String deviceType) {
		this.deviceType = deviceType;
	}

	private final String deviceType;

	/**
	 * @return the code
	 */
	public String getDeviceType() {
		return deviceType;
	}
	

}
