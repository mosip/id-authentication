package io.mosip.registration.processor.core.auth.dto;

import lombok.Data;

@Data
public class AuthTypeDTO {

	private boolean bio;
	
	private boolean demo = false;
	
	private boolean otp = false;;

	private boolean pin = false;

}
