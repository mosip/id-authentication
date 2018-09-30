package org.mosip.registration.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;


@Data
@Entity
@Table(schema="reg", name = "user_role")
public class RegistrationUserRole extends RegistrationCommonFields {
	@EmbeddedId
	RegistrationUserRoleID registrationUserRoleID;
}
