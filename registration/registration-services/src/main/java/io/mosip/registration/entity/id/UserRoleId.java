package io.mosip.registration.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.mosip.registration.entity.UserRole;
import lombok.Data;

/**
 * composite key for {@link UserRole}
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
@Data
public class UserRoleId implements Serializable {

	private static final long serialVersionUID = -8072043172665654382L;

	@Column(name = "usr_id")
	private String usrId;
	@Column(name = "role_code")
	private String roleCode;

}
