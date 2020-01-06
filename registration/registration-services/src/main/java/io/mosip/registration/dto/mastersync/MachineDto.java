
package io.mosip.registration.dto.mastersync;

import java.time.LocalDateTime;

/**
 * Response dto for Machine Detail
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */


public class MachineDto extends MasterSyncBaseDto{

	/**
	 * Field for machine id
	 */
	private String id;
	/**
	 * Field for machine name
	 */
	private String name;
	/**
	 * Field for machine serial number
	 */
	private String serialNum;
	/**
	 * Field for machine mac address
	 */
	private String macAddress;
	/**
	 * Field for machine IP address
	 */
	private String ipAddress;
	/**
	 * Field for machine specification Id
	 */
	private String machineSpecId;
	/**
	 * Field for language code
	 */
	private String langCode;
	/**
	 * Field for is active
	 */
	private Boolean isActive;
	/**
	 * Field for is validity of the Device
	 */
	private LocalDateTime validityDateTime;
	/**
	 * Field for is key index
	 */
	private String keyIndex;
	/**
	 * Field for is public Key
	 */
	private String publicKey;
	
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
	 * @return the machineSpecId
	 */
	public String getMachineSpecId() {
		return machineSpecId;
	}
	/**
	 * @param machineSpecId the machineSpecId to set
	 */
	public void setMachineSpecId(String machineSpecId) {
		this.machineSpecId = machineSpecId;
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
	public Boolean getIsActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	/**
	 * @return the validityDateTime
	 */
	public LocalDateTime getValidityDateTime() {
		return validityDateTime;
	}
	/**
	 * @param validityDateTime the validityDateTime to set
	 */
	public void setValidityDateTime(LocalDateTime validityDateTime) {
		this.validityDateTime = validityDateTime;
	}
	/**
	 * @return the keyIndex
	 */
	public String getKeyIndex() {
		return keyIndex;
	}
	/**
	 * @param keyIndex the keyIndex to set
	 */
	public void setKeyIndex(String keyIndex) {
		this.keyIndex = keyIndex;
	}
	/**
	 * @return the publicKey
	 */
	public String getPublicKey() {
		return publicKey;
	}
	/**
	 * @param publicKey the publicKey to set
	 */
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	
	
	

}
