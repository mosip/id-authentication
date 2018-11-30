package io.mosip.registration.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(schema = "reg", name = "reg_center_user_machine")
public class UserMachineMapping extends RegistrationCommonFields implements Serializable {

	/**
	 * serial Version UID
	 */
	private static final long serialVersionUID = 8686723876595925323L;

	@EmbeddedId
	@Column(name = "pk_cntrmusr_usr_id")
	private UserMachineMappingID userMachineMappingId;

	@ManyToOne
	@JoinColumn(name = "usr_id", nullable = false, insertable = false, updatable = false)
	private RegistrationUserDetail registrationUserDetail;

	@ManyToOne
	@JoinColumn(name = "machine_id", nullable = false, insertable = false, updatable = false)
	private MachineMaster machineMaster;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	private Timestamp deletedDateTime;

	/**
	 * @return the userMachineMappingId
	 */
	public UserMachineMappingID getUserMachineMappingId() {
		return userMachineMappingId;
	}

	/**
	 * @param userMachineMappingId
	 *            the userMachineMappingId to set
	 */
	public void setUserMachineMappingId(UserMachineMappingID userMachineMappingId) {
		this.userMachineMappingId = userMachineMappingId;
	}

	/**
	 * @return the registrationUserDetail
	 */
	public RegistrationUserDetail getRegistrationUserDetail() {
		return registrationUserDetail;
	}

	/**
	 * @param registrationUserDetail
	 *            the registrationUserDetail to set
	 */
	public void setRegistrationUserDetail(RegistrationUserDetail registrationUserDetail) {
		this.registrationUserDetail = registrationUserDetail;
	}

	/**
	 * @return the machineMaster
	 */
	public MachineMaster getMachineMaster() {
		return machineMaster;
	}

	/**
	 * @param machineMaster
	 *            the machineMaster to set
	 */
	public void setMachineMaster(MachineMaster machineMaster) {
		this.machineMaster = machineMaster;
	}

	/**
	 * @return the isDeleted
	 */
	public Boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted
	 *            the isDeleted to set
	 */
	public void setDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the deletedDateTime
	 */
	public Timestamp getDeletedDateTime() {
		return deletedDateTime;
	}

	/**
	 * @param deletedDateTime
	 *            the deletedDateTime to set
	 */
	public void setDeletedDateTime(Timestamp deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}
	

}