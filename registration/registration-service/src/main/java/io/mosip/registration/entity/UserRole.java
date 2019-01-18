package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * UserRole entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "user_role")
@Getter
@Setter
public class UserRole extends RegistrationCommonFields {

	@EmbeddedId
	private UserRoleID userRoleID;

	@Column(name = "lang_code")
	private String langCode;

	@ManyToOne
	@JoinColumn(name = "usr_id", nullable = false, insertable = false, updatable = false)
	private UserDetail userDetail;

}
