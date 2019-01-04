package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Sreekar chukka
 * @since 1.0.0
 */
@Entity
@Table(name = "machine_spec", schema = "reg")
public class MasterMachineSpecification extends MasterSyncBaseEntity implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "name")
	private String name;

	@Column(name = "brand")
	private String brand;

	@Column(name = "model")
	private String model;

	@Column(name = "mtyp_code")
	private String machineTypeCode;

	@Column(name = "min_driver_ver")
	private String minDriverversion;

	@Column(name = "descr")
	private String description;

	@Column(name = "lang_code")
	private String langCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "mtyp_code", referencedColumnName = "code", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false) })
	private MasterMachineType machineType;

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
	 * @return the brand
	 */
	public String getBrand() {
		return brand;
	}

	/**
	 * @param brand the brand to set
	 */
	public void setBrand(String brand) {
		this.brand = brand;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the machineTypeCode
	 */
	public String getMachineTypeCode() {
		return machineTypeCode;
	}

	/**
	 * @param machineTypeCode the machineTypeCode to set
	 */
	public void setMachineTypeCode(String machineTypeCode) {
		this.machineTypeCode = machineTypeCode;
	}

	/**
	 * @return the minDriverversion
	 */
	public String getMinDriverversion() {
		return minDriverversion;
	}

	/**
	 * @param minDriverversion the minDriverversion to set
	 */
	public void setMinDriverversion(String minDriverversion) {
		this.minDriverversion = minDriverversion;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @return the machineType
	 */
	public MasterMachineType getMachineType() {
		return machineType;
	}

	/**
	 * @param machineType the machineType to set
	 */
	public void setMachineType(MasterMachineType machineType) {
		this.machineType = machineType;
	}
	
	

}
