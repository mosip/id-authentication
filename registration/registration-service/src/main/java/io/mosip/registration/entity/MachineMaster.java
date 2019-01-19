package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Machine master entity details
 * 
 * @author Yaswanth S
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "machine_master")
public class MachineMaster extends RegistrationCommonFields {

	@Id
	@Column(name = "id")
	private String id;
	@Column(name = "name")
	private String name;
	@Column(name = "mac_address")
	private String macAddress;
	@Column(name = "serial_num")
	private String serialNum;
	@Column(name = "ip_address")
	private String ipAddress;
	@Column(name = "mspec_id")
	private String mspecId;
	@Column(name = "lang_code")
	private String langCode;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp deletedDateTime;
	
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
	 * @return the mspecId
	 */
	public String getMspecId() {
		return mspecId;
	}
	/**
	 * @param mspecId the mspecId to set
	 */
	public void setMspecId(String mspecId) {
		this.mspecId = mspecId;
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
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	/**
	 * @return the deletedDateTime
	 */
	public Timestamp getDeletedDateTime() {
		return deletedDateTime;
	}
	/**
	 * @param deletedDateTime the deletedDateTime to set
	 */
	public void setDeletedDateTime(Timestamp deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}
	
		
}
