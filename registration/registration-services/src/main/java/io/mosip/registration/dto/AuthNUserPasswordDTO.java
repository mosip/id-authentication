package io.mosip.registration.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The DTO class required for the Authentication Token Web-Service
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthNUserPasswordDTO extends AuthNDTO {

	private String userName;
	private String password;
	private String appId;

}
