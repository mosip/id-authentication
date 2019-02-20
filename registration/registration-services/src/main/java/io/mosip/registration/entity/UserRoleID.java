package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

/**
 * Composite key for UserRole entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
@Data
public class UserRoleID implements Serializable {

	private static final long serialVersionUID = -8072043172665654382L;

	@Column(name = "usr_id")
	private String usrId;
	@Column(name = "role_code")
	private String roleCode;
	
}
