package io.mosip.registration.entity.mastersync;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * Entity for Machine Details
 * 
 * @author Sreekar chukka
 * @since 1.0.0
 */
@Entity
@Table(name = "machine_master", schema = "reg")
public class MasterMachine extends MasterSyncBaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5585825705521742941L;

	/**
	 * Field for machine ID
	 */
	@Id
	@Column(name = "id")
	private String id;

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
	 * Field for language code
	 */
	@Column(name = "lang_code")
	private String langCode;

	/**
	 * Field for validity end Date and Time for machine
	 */
	@Column(name = "validity_end_dtimes")
	private LocalDateTime validityDateTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "mspec_id", referencedColumnName = "id", insertable = false, updatable = false) })
	private MasterMachineSpecification machineSpecification;

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
	 * @return the machineSpecification
	 */
	public MasterMachineSpecification getMachineSpecification() {
		return machineSpecification;
	}

	/**
	 * @param machineSpecification the machineSpecification to set
	 */
	public void setMachineSpecification(MasterMachineSpecification machineSpecification) {
		this.machineSpecification = machineSpecification;
	}
	
	

}
