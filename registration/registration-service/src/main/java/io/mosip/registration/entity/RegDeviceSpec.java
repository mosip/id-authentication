package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * RegDeviceSpec entity details
 * 
 * @author Brahmanada Reddy
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "device_spec", schema = "reg")
public class RegDeviceSpec extends RegistrationCommonFields {
	@Id
	@Column(name = "id")
	private String id;
	@Column(name = "name")
	private String name;
	@Column(name = "brand")
	private String brand;
	@Column(name = "model")
	private String model;
	@Column(name = "min_driver_ver")
	private String minDriverVersion;
	@Column(name = "descr")
	private String description;
	@Column(name = "lang_code")
	private String languageCode;
	@Column(name = "is_deleted")
	@Type(type = "true_false")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp deletedTime;
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "dtyp_code",insertable = false, updatable = false),
		@JoinColumn(name = "lang_code", insertable = false, updatable = false)
	})
	private RegDeviceType regDeviceType;

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
	 * @return the brand
	 */
	public String getBrand() {
		return brand;
	}

	/**
	 * @param brand
	 *            the brand to set
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
	 * @param model
	 *            the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the minDriverVersion
	 */
	public String getMinDriverVersion() {
		return minDriverVersion;
	}

	/**
	 * @param minDriverVersion
	 *            the minDriverVersion to set
	 */
	public void setMinDriverVersion(String minDriverVersion) {
		this.minDriverVersion = minDriverVersion;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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

	/**
	 * @return the regDeviceType
	 */
	public RegDeviceType getRegDeviceType() {
		return regDeviceType;
	}

	/**
	 * @param regDeviceType
	 *            the regDeviceType to set
	 */
	public void setRegDeviceType(RegDeviceType regDeviceType) {
		this.regDeviceType = regDeviceType;
	}

}
