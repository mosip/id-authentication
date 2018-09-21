package org.mosip.auth.core.dto.indauth;

import java.io.Serializable;

import lombok.Data;

/**
 *{@code OtpAuthResponseDTO} is the DTO class for the OTP Authentication Response.
 *
 * @author Mahesh Kumar
 */
@Data
public class OtpAuthResponseDTO implements Serializable {

	private static final long serialVersionUID = -636299122810622583L;
	
	private AuthResponseDTO authResp;

}
