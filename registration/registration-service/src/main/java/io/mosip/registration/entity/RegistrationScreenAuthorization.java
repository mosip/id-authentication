package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

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

	@Column(name = "is_permitted", nullable = true, updatable = false)
	@Type(type = "true_false")
	private boolean isPermitted;
	@Column(name = "is_deleted", nullable = true, updatable = true)
	@Type(type = "true_false")
	private boolean isDeleted;
	@Column(name = "del_dtimes", nullable = true, updatable = true)
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
	public boolean isPermitted() {
		return isPermitted;
	}

	/**
	 * @param isPermitted
	 *            the isPermitted to set
	 */
	public void setPermitted(boolean isPermitted) {
		this.isPermitted = isPermitted;
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
