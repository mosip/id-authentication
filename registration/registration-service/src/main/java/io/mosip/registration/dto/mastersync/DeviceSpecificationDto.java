package io.mosip.registration.dto.mastersync;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 */

public class DeviceSpecificationDto extends MasterSyncBaseDto {
	private String id;
	private String name;
	private String brand;
	private String model;
	private String deviceTypeCode;
	private String minDriverversion;
	private String description;
	private String langCode;
	private Boolean isActive;

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

}
