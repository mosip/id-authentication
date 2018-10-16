package io.mosip.registration.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * RegistrationUserRole entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "user_role")
public class RegistrationUserRole extends RegistrationCommonFields {

	@EmbeddedId
	private RegistrationUserRoleId registrationUserRoleId;
	
	@ManyToOne
	@JoinColumn(name = "usr_id", nullable = false, insertable = false, updatable = false)
	private RegistrationUserDetail registrationUserDetail;	

	public RegistrationUserDetail getRegistrationUserDetail() {
		return registrationUserDetail;
	}

	public void setRegistrationUserDetail(RegistrationUserDetail registrationUserDetail) {
		this.registrationUserDetail = registrationUserDetail;
	}

	/**
	 * @return the registrationUserRoleId
	 */
	public RegistrationUserRoleId getRegistrationUserRoleId() {
		return registrationUserRoleId;
	}

	/**
	 * @param registrationUserRoleId
	 *            the registrationUserRoleId to set
	 */
	public void setRegistrationUserRoleId(RegistrationUserRoleId registrationUserRoleId) {
		this.registrationUserRoleId = registrationUserRoleId;
	}
	
	@Override
	public String toString() {
		return "RegistrationUserRole [registrationUserRoleId=" + registrationUserRoleId + "]";

	}

}
