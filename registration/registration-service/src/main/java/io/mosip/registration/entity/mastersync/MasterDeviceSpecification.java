package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author Sreekar chukka
 * @since 1.0.0
 *
 */

@Entity
@Table(name = "device_spec", schema = "reg")
public class MasterDeviceSpecification extends MasterSyncBaseEntity implements Serializable {
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
	
	@Column(name = "dtyp_code")
	private String deviceTypeCode;
	
	@Column(name = "min_driver_ver")
	private String minDriverversion;
	
	@Column(name = "descr")
	private String description;
	
	@Column(name = "lang_code")
	private String langCode;

	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "dtyp_code", referencedColumnName = "code",insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code",insertable = false, updatable = false) })
	private MasterDeviceType deviceType;

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
	 * @return the deviceType
	 */
	public MasterDeviceType getDeviceType() {
		return deviceType;
	}

	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(MasterDeviceType deviceType) {
		this.deviceType = deviceType;
	}
	
	

}
