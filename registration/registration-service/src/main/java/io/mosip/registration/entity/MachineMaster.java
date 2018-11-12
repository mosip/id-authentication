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
	@Column(name = "id", length = 64, nullable = false, updatable = false)
	private String id;
	@Column(name = "name", length = 64, nullable = false)
	private String name;
	@Column(name = "mac_address", length = 17, nullable = false)
	private String macAddress;
	@Column(name = "ip_address", length = 16, nullable = false)
	private String ipAddress;
	@Column(name = "serial_num", length = 64, nullable = false)
	private String serialNum;
	@Column(name = "typ", length = 64)
	private String typ;
	@Column(name = "lang_code", length = 3, nullable = false)
	private String langCode;
	@Column(name = "is_deleted")
	private boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp deletedDateTime;
	
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * @return the isDeleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setDeleted(boolean isDeleted) {
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
