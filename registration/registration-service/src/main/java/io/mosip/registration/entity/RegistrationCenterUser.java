package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * RegistrationCenterUser entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "reg_center_user")
public class RegistrationCenterUser extends RegistrationCommonFields {

	@EmbeddedId
	@Column(name = "pk_cntrusr_usr_id")
	private RegistrationCenterUserId registrationCenterUserId;

	@OneToOne
	@JoinColumn(name = "usr_id", nullable = false, insertable = false, updatable = false)
	private RegistrationUserDetail registrationUserDetail;

	@OneToOne
	@JoinColumn(name = "regcntr_id", nullable = false, insertable = false, updatable = false)
	private RegistrationCenter registrationCenter;

	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;

	/**
	 * @return the registrationCenterUserId
	 */
	public RegistrationCenterUserId getRegistrationCenterUserId() {
		return registrationCenterUserId;
	}

	/**
	 * @param registrationCenterUserId
	 *            the registrationCenterUserId to set
	 */
	public void setRegistrationCenterUserId(RegistrationCenterUserId registrationCenterUserId) {
		this.registrationCenterUserId = registrationCenterUserId;
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
	 * @return the registrationCenter
	 */
	public RegistrationCenter getRegistrationCenter() {
		return registrationCenter;
	}

	/**
	 * @param registrationCenter
	 *            the registrationCenter to set
	 */
	public void setRegistrationCenter(RegistrationCenter registrationCenter) {
		this.registrationCenter = registrationCenter;
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
	 * @return the delDtimes
	 */
	public Timestamp getDelDtimes() {
		return delDtimes;
	}

	/**
	 * @param delDtimes
	 *            the delDtimes to set
	 */
	public void setDelDtimes(Timestamp delDtimes) {
		this.delDtimes = delDtimes;
	}

}
