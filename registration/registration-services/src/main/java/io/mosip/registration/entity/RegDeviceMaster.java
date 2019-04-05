package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import io.mosip.registration.entity.id.RegMachineSpecId;

/**
 * The Entity Class for Reg Device Master details
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "device_master", schema = "reg")
public class RegDeviceMaster extends RegistrationCommonFields {

	@EmbeddedId
	private RegMachineSpecId regMachineSpecId;

	@Column(name = "name")
	private String name;
	@Column(name = "mac_address")
	private String macAddress;
	@Column(name = "serial_num")
	private String serialNum;
	@Column(name = "ip_address")
	private String ipAddress;
	@Column(name = "dspec_id")
	private String deviceSpecId;
	@Column(name = "is_deleted")
	@Type(type = "true_false")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp deletedTime;
	

	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "dspec_id", referencedColumnName = "id", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false) })
	private RegDeviceSpec regDeviceSpec;
	
	

	/**
	 * @return the regMachineSpecId
	 */
	public RegMachineSpecId getRegMachineSpecId() {
		return regMachineSpecId;
	}

	/**
	 * @param regMachineSpecId the regMachineSpecId to set
	 */
	public void setRegMachineSpecId(RegMachineSpecId regMachineSpecId) {
		this.regMachineSpecId = regMachineSpecId;
	}

	@Column(name = " validity_end_dtimes")
	private Timestamp validityEndDtimes;

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
	 * @return the validityEndDtimes
	 */
	public Timestamp getValidityEndDtimes() {
		return validityEndDtimes;
	}

	/**
	 * @param validityEndDtimes the validityEndDtimes to set
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
	 * @param regDeviceSpec the regDeviceSpec to set
	 */
	public void setRegDeviceSpec(RegDeviceSpec regDeviceSpec) {
		this.regDeviceSpec = regDeviceSpec;
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
	 * @return the deletedTime
	 */
	public Timestamp getDeletedTime() {
		return deletedTime;
	}

	/**
	 * @param deletedTime the deletedTime to set
	 */
	public void setDeletedTime(Timestamp deletedTime) {
		this.deletedTime = deletedTime;
	}

}
