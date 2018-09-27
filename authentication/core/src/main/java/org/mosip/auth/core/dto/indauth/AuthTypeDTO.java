package org.mosip.auth.core.dto.indauth;

import lombok.Data;

/**
 * 
 * @author Rakesh Roshan
 */
@Data
public class AuthTypeDTO {

	private Boolean id;
	
	private Boolean ad;

	private Boolean pin;

	private Boolean bio;

	private Boolean otp;

}
