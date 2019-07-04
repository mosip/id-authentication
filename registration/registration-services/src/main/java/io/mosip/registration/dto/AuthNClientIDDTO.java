package io.mosip.registration.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The DTO class required to send the OTP
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthNClientIDDTO extends AuthNDTO {

	private String clientId;
	private String secretKey;
	private String appId;

}
