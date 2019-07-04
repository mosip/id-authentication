package io.mosip.registration.dto.mastersync;

/**
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
public class RegistrationCenterMachineDeviceDto extends MasterSyncBaseDto {

	private String regCenterId;

	private String machineId;

	private String deviceId;

	private Boolean isActive;

	private String langCode;

	/**
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * @param langCode the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * @return the regCenterId
	 */
	public String getRegCenterId() {
		return regCenterId;
	}

	/**
	 * @param regCenterId the regCenterId to set
	 */
	public void setRegCenterId(String regCenterId) {
		this.regCenterId = regCenterId;
	}

	/**
	 * @return the machineId
	 */
	public String getMachineId() {
		return machineId;
	}

	/**
	 * @param machineId the machineId to set
	 */
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
