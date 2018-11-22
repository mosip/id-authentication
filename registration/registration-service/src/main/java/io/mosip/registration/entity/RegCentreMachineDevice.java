package io.mosip.registration.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * RegCentreMachineDevice entity details
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 */

@Entity
@Table(name = "reg_center_machine_device", schema = "reg")
public class RegCentreMachineDevice extends RegistrationCommonFields implements Serializable {

	private static final long serialVersionUID = -2355800981218228906L;
	@EmbeddedId
	private RegCentreMachineDeviceId regCentreMachineDeviceId;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp deletedTime;
	@ManyToOne
	@JoinColumn(name = "device_id", insertable = false, updatable = false)
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
	 * @return the regCentreMachineDeviceId
	 */
	public RegCentreMachineDeviceId getRegCentreMachineDeviceId() {
		return regCentreMachineDeviceId;
	}

	/**
	 * @param regCentreMachineDeviceId
	 *            the regCentreMachineDeviceId to set
	 */
	public void setRegCentreMachineDeviceId(RegCentreMachineDeviceId regCentreMachineDeviceId) {
		this.regCentreMachineDeviceId = regCentreMachineDeviceId;
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
