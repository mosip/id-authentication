package io.mosip.registration.entity;

import java.io.Serializable;

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
	@Column(name = "pk_cntrum_usr_id")
	private UserMachineMappingID userMachineMappingId;

	@ManyToOne
	@JoinColumn(name = "usr_id", nullable = false, insertable = false, updatable = false)
	private RegistrationUserDetail registrationUserDetail;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "del_dtimesz")
	private String deletedDateTime;

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
	 * @return the isDeleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted
	 *            the isDeleted to set
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the deletedDateTime
	 */
	public String getDeletedDateTime() {
		return deletedDateTime;
	}

	/**
	 * @param deletedDateTime
	 *            the deletedDateTime to set
	 */
	public void setDeletedDateTime(String deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}
	

}