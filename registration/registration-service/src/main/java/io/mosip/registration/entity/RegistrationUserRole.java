package io.mosip.registration.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.registration.entity.RegistrationCommonFields;
import io.mosip.registration.entity.RegistrationUserRoleId;

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
	RegistrationUserRoleId registrationUserRoleId;

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

}
