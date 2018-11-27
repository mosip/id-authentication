package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * RegDeviceType entity details
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "device_type", schema = "reg")
public class RegDeviceType extends RegistrationCommonFields {
	@EmbeddedId
	RegDeviceTypeId regDeviceTypeId;
	@Column(name = "name")
	private String name;
	@Column(name = "descr")
	private String description;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp deletedTime;

	/**
	 * @return the regDeviceTypeId
	 */
	public RegDeviceTypeId getRegDeviceTypeId() {
		return regDeviceTypeId;
	}

	/**
	 * @param regDeviceTypeId
	 *            the regDeviceTypeId to set
	 */
	public void setRegDeviceTypeId(RegDeviceTypeId regDeviceTypeId) {
		this.regDeviceTypeId = regDeviceTypeId;
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
