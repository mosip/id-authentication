package io.mosip.registration.dto.mastersync;

import java.time.LocalDateTime;

/**
 * Response dto for Device Details for given Language code and device type
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */


public class DeviceLangCodeDtypeDto extends MasterSyncBaseDto {

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
	 * Field for Ip Address
	 */
	private String ipAddress;
	/**
	 * Field for device specification Id
	 */
	private String dspecId;
	/**
	 * Field for device mac address
	 */
	private String macAddress;
	/**
	 * Field for language code
	 */
	private String langCode;
	/**
	 * Field for is active
	 */
	private boolean isActive;

	/**
	 * Field for device type
	 */
	private String deviceTypeCode;
	/**
	 * Field to hold date and time for Validity of the Device
	 */
	private LocalDateTime validityEndDateTime;
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
	 * @return the dspecId
	 */
	public String getDspecId() {
		return dspecId;
	}
	/**
	 * @param dspecId the dspecId to set
	 */
	public void setDspecId(String dspecId) {
		this.dspecId = dspecId;
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
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	/**
	 * @return the deviceTypeCode
	 */
	public String getDeviceTypeCode() {
		return deviceTypeCode;
	}
	/**
	 * @param deviceTypeCode the deviceTypeCode to set
	 */
	public void setDeviceTypeCode(String deviceTypeCode) {
		this.deviceTypeCode = deviceTypeCode;
	}
	/**
	 * @return the validityEndDateTime
	 */
	public LocalDateTime getValidityEndDateTime() {
		return validityEndDateTime;
	}
	/**
	 * @param validityEndDateTime the validityEndDateTime to set
	 */
	public void setValidityEndDateTime(LocalDateTime validityEndDateTime) {
		this.validityEndDateTime = validityEndDateTime;
	}
	
	

}
