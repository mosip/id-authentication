package io.mosip.registration.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.mosip.registration.entity.id.RegMachineSpecId;

/**
 * This Entity Class conatins list of machine related data[mac address, serial number, machine name...] 
 * with respect to language code.
 * The data for this table will come through sync from server master table.
 * 
 * @author Yaswanth S
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "machine_master")
public class MachineMaster extends RegistrationCommonFields implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5585825705521742941L;

	/**
	 * Field for machine ID
	 */
	@EmbeddedId
	private RegMachineSpecId regMachineSpecId;

	/**
	 * Field for machine name
	 */
	@Column(name = "name")
	private String name;

	/**
	 * Field for machine serial number
	 */
	@Column(name = "serial_num")
	private String serialNum;

	/**
	 * Field for machine ip address
	 */
	@Column(name = "ip_address")
	private String ipAddress;
	/**
	 * Field for machine mac address
	 */
	@Column(name = "mac_address")
	private String macAddress;

	/**
	 * Field for machine specific id
	 */
	@Column(name = "mspec_id")
	private String machineSpecId;

	/**
	 * Field for validity end Date and Time for machine
	 */
	@Column(name = "validity_end_dtimes")
	private LocalDateTime validityDateTime;

	@Column(name = "public_key", columnDefinition = "CLOB")
	private String publicKey;

	@Column(name = "key_index")
	private String keyIndex;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "mspec_id", referencedColumnName = "id", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false) })
	private RegMachineSpec machineSpecification;

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

	public RegMachineSpec getMachineSpecification() {
		return machineSpecification;
	}

	public void setMachineSpecification(RegMachineSpec machineSpecification) {
		this.machineSpecification = machineSpecification;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getKeyIndex() {
		return keyIndex;
	}

	public void setKeyIndex(String keyIndex) {
		this.keyIndex = keyIndex;
	}

}
