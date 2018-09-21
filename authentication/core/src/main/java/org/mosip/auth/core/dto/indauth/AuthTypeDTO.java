package org.mosip.auth.core.dto.indauth;

import lombok.Data;

/**
 * 
 * @author Rakesh Roshan
 */
@Data
public class AuthTypeDTO {

	private Boolean piAuth;

	private Boolean paAuth;

	private Boolean pinAuth;

	private Boolean bioAuth;

	private Boolean otpAuth;

}
