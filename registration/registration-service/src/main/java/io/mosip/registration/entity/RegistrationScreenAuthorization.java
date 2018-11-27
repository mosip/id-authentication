package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * RegistrationScreenAuthorization entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "screen_authorization")
public class RegistrationScreenAuthorization extends RegistrationCommonFields {

	@EmbeddedId
	private RegistrationScreenAuthorizationId registrationScreenAuthorizationId;

	@Column(name = "is_permitted")
	private Boolean isPermitted;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;

	/**
	 * @return the registrationScreenAuthorizationId
	 */
	public RegistrationScreenAuthorizationId getRegistrationScreenAuthorizationId() {
		return registrationScreenAuthorizationId;
	}

	/**
	 * @param registrationScreenAuthorizationId
	 *            the registrationScreenAuthorizationId to set
	 */
	public void setRegistrationScreenAuthorizationId(
			RegistrationScreenAuthorizationId registrationScreenAuthorizationId) {
		this.registrationScreenAuthorizationId = registrationScreenAuthorizationId;
	}

	/**
	 * @return the isPermitted
	 */
	public Boolean isPermitted() {
		return isPermitted;
	}

	/**
	 * @param isPermitted
	 *            the isPermitted to set
	 */
	public void setPermitted(Boolean isPermitted) {
		this.isPermitted = isPermitted;
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
