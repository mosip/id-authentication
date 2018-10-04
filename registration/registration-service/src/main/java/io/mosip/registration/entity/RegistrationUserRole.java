package io.mosip.registration.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.registration.entity.RegistrationCommonFields;
import io.mosip.registration.entity.RegistrationUserRoleID;

/**
 * RegistrationUserRole entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema="reg", name = "user_role")
public class RegistrationUserRole extends RegistrationCommonFields {
	@EmbeddedId
	RegistrationUserRoleID registrationUserRoleID;

	public RegistrationUserRoleID getRegistrationUserRoleID() {
		return registrationUserRoleID;
	}

	public void setRegistrationUserRoleID(RegistrationUserRoleID registrationUserRoleID) {
		this.registrationUserRoleID = registrationUserRoleID;
	}
	
}
