package io.mosip.registration.processor.core.packet.dto;

/**
 * The Class DeviceDetails.
 */
public class DeviceDetails {

	/** The device code. */
	private String deviceCode;

	/** The device provider id. */
	private String deviceServiceVersion;

	/** The device service version. */
	private DigitalIdDto digitalId;

	/**
	 * Gets the device code.
	 *
	 * @return the device code
	 */
	public String getDeviceCode() {
		return deviceCode;
	}

	/**
	 * Sets the device code.
	 *
	 * @param deviceCode
	 *            the new device code
	 */
	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	/**
	 * Gets the device service version.
	 *
	 * @return the device service version
	 */
	public String getDeviceServiceVersion() {
		return deviceServiceVersion;
	}

	/**
	 * Sets the device service version.
	 *
	 * @param deviceServiceVersion
	 *            the new device service version
	 */
	public void setDeviceServiceVersion(String deviceServiceVersion) {
		this.deviceServiceVersion = deviceServiceVersion;
	}

	public DigitalIdDto getDigitalId() {
		return digitalId;
	}

	public void setDigitalId(DigitalIdDto digitalId) {
		this.digitalId = digitalId;
	}

}
