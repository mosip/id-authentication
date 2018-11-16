package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * RegDeviceMaster entity details
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "device_master", schema = "reg")
public class RegDeviceMaster extends RegistrationCommonFields {
	@Id
	@Column(name = "id")
	private String id;
	@Column(name = "name")
	private String name;
	@Column(name = "mac_address")
	private String macAdress;
	@Column(name = "serial_num")
	private String serialNumber;
	@Column(name = "ip_address")
	private String ipAddress;
	@Column(name = "lang_code")
	private String languageCode;
	@Column(name = "is_deleted")
	@Type(type = "true_false")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp deletedTime;
	@ManyToOne
	@JoinColumn(name = "dspec_id")
	private RegDeviceSpec regDeviceSpec;
	@Column(name = " validity_end_dtimes")
	private Timestamp validityEndDtimes;

	/**
	 * @return the validityEndDtimes
	 */
	public Timestamp getValidityEndDtimes() {
		return validityEndDtimes;
	}

	/**
	 * @param validityEndDtimes
	 *            the validityEndDtimes to set
	 */
	public void setValidityEndDtimes(Timestamp validityEndDtimes) {
		this.validityEndDtimes = validityEndDtimes;
	}

	/**
	 * @return the regDeviceSpec
	 */
	public RegDeviceSpec getRegDeviceSpec() {
		return regDeviceSpec;
	}

	/**
	 * @param regDeviceSpec
	 *            the regDeviceSpec to set
	 */
	public void setRegDeviceSpec(RegDeviceSpec regDeviceSpec) {
		this.regDeviceSpec = regDeviceSpec;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
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
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the macAdress
	 */
	public String getMacAdress() {
		return macAdress;
	}

	/**
	 * @param macAdress
	 *            the macAdress to set
	 */
	public void setMacAdress(String macAdress) {
		this.macAdress = macAdress;
	}

	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * @param serialNumber
	 *            the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the languageCode
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * @param languageCode
	 *            the languageCode to set
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	/**
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted
	 *            the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the deletedTime
	 */
	public Timestamp getDeletedTime() {
		return deletedTime;
	}

	/**
	 * @param deletedTime
	 *            the deletedTime to set
	 */
	public void setDeletedTime(Timestamp deletedTime) {
		this.deletedTime = deletedTime;
	}

}
