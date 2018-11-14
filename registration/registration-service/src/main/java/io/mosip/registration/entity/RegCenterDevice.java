package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * RegCenterDevice entity details
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */

@Entity
@Table(name = "reg_center_device", schema = "reg")
public class RegCenterDevice extends RegistrationCommonFields {
	@EmbeddedId
	private RegCenterDeviceId regCenterDeviceId;
	@Column(name = "is_deleted")
	@Type(type = "true_false")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp deletedTime;

	@ManyToOne
	@JoinColumn(name = "device_id", insertable = false)
	private RegDeviceMaster regDeviceMaster;

	/**
	 * @return the regDeviceMaster
	 */
	public RegDeviceMaster getRegDeviceMaster() {
		return regDeviceMaster;
	}

	/**
	 * @param regDeviceMaster
	 *            the regDeviceMaster to set
	 */
	public void setRegDeviceMaster(RegDeviceMaster regDeviceMaster) {
		this.regDeviceMaster = regDeviceMaster;
	}

	/**
	 * @return the regCenterDeviceId
	 */
	public RegCenterDeviceId getRegCenterDeviceId() {
		return regCenterDeviceId;
	}

	/**
	 * @param regCenterDeviceId
	 *            the regCenterDeviceId to set
	 */
	public void setRegCenterDeviceId(RegCenterDeviceId regCenterDeviceId) {
		this.regCenterDeviceId = regCenterDeviceId;
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
