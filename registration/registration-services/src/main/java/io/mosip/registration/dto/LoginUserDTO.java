package io.mosip.registration.dto;

import lombok.Data;

/**
 * DTO class for login info
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
@Data
public class LoginUserDTO {

	private String userId;
	private String password;
	private String otp;

}
