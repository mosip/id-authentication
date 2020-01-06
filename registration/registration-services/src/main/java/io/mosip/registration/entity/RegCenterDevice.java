package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.mosip.registration.entity.id.RegCenterDeviceId;

/**
 * This Entity Class contains list of device ids which are mapped to corresponding center id .
 * The data for this table will come through sync from server master table.
 *
 * @author Brahmananda Reddy
 * @since 1.0.0
 */
@Entity
@Table(name = "reg_center_device", schema = "reg")
public class RegCenterDevice extends RegistrationCommonFields {

	@EmbeddedId
	private RegCenterDeviceId regCenterDeviceId;

	/** The reg device master. */
	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "device_id", referencedColumnName = "id", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false) })
	private RegDeviceMaster regDeviceMaster;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The deleted time. */
	@Column(name = "del_dtimes")
	private Timestamp deletedTime;

	/** The lang code. */
	@Column(name = "lang_code")
	private String langCode;

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
	 * @return the regCenterDeviceId
	 */
	public RegCenterDeviceId getRegCenterDeviceId() {
		return regCenterDeviceId;
	}

	/**
	 * @param regCenterDeviceId the regCenterDeviceId to set
	 */
	public void setRegCenterDeviceId(RegCenterDeviceId regCenterDeviceId) {
		this.regCenterDeviceId = regCenterDeviceId;
	}

	/**
	 * @return the regDeviceMaster
	 */
	public RegDeviceMaster getRegDeviceMaster() {
		return regDeviceMaster;
	}

	/**
	 * @param regDeviceMaster the regDeviceMaster to set
	 */
	public void setRegDeviceMaster(RegDeviceMaster regDeviceMaster) {
		this.regDeviceMaster = regDeviceMaster;
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
