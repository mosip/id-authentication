package io.mosip.registration.entity.mastersync;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * Entity for Device Details
 * 
 */
@Entity
@Table(name = "device_master", schema = "reg")
public class MasterDevice  extends MasterSyncBaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5585825705521742941L;

	/**
	 * Field for device ID
	 */
	@Id
	@Column(name = "id")
	private String id;

	/**
	 * Field for device name
	 */
	@Column(name = "name")
	private String name;

	/**
	 * Field for device serial number
	 */
	@Column(name = "serial_num")
	private String serialNum;

	/**
	 * Field for device ip address
	 */
	@Column(name = "ip_address")
	private String ipAddress;

	/**
	 * Field for device mac address
	 */
	@Column(name = "mac_address")
	private String macAddress;

	/**
	 * Field for device specific id
	 */
	@Column(name = "dspec_id")
	private String deviceSpecId;

	/**
	 * Field for language code
	 */
	@Column(name = "lang_code")
	private String langCode;

	/**
	 * Field to hold date and time for Validity of the Device
	 */
	@Column(name="validity_end_dtimes")
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
