package io.mosip.admin.securitypolicy.dto;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

/**
 * Auth factor dto, contains the authentication mode for the user.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
public class AuthFactorsDto implements Serializable {
	private static final long serialVersionUID = 3605540533466534622L;
	/**
	 * field to contain authentication types.
	 */
	private Set<String> authTypes;
}
