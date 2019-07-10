package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.mosip.registration.entity.id.UserRoleId;
import lombok.Getter;
import lombok.Setter;

/**
 * The Entity Class for User Role details
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
	private UserRoleId userRoleId;

	@Column(name = "lang_code")
	private String langCode;

	@ManyToOne
	@JoinColumn(name = "usr_id", nullable = false, insertable = false, updatable = false)
	private UserDetail userDetail;

}
