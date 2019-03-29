package io.mosip.registration.dto.mastersync;

/**
 * Response dto for Device Detail
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public class DeviceDto extends MasterSyncBaseDto {

	/**
	 * Field for device id
	 */
	private String id;
	/**
	 * Field for device name
	 */
	private String name;
	/**
	 * Field for device serial number
	 */
	private String serialNum;
	/**
	 * Field for device device specification Id
	 */
	private String deviceSpecId;
	/**
	 * Field for device mac address
	 */
	private String macAddress;
	/**
	 * Field for device ip address
	 */
	private String ipAddress;
	/**
	 * Field for language code
	 */
	private String langCode;
	
	private String validityDateTime;

	private Boolean isActive;
	
	
	/**
	 * @return the validityDateTime
	 */
	public String getValidityDateTime() {
		return validityDateTime;
	}

	/**
	 * @param validityDateTime the validityDateTime to set
	 */
	public void setValidityDateTime(String validityDateTime) {
		this.validityDateTime = validityDateTime;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the serialNum
	 */
	public String getSerialNum() {
		return serialNum;
	}

	/**
	 * @param serialNum the serialNum to set
	 */
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	/**
	 * @return the deviceSpecId
	 */
	public String getDeviceSpecId() {
		return deviceSpecId;
	}

	/**
	 * @param deviceSpecId the deviceSpecId to set
	 */
	public void setDeviceSpecId(String deviceSpecId) {
		this.deviceSpecId = deviceSpecId;
	}

	/**
	 * @return the macAddress
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * @param macAddress the macAddress to set
	 */
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

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
}
