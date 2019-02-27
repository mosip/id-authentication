package io.mosip.authentication.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Prem Kumar
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalFactorsDTO {
	/** The Value for OTP */
	private String totp;
		
	/** The Value for Static Pin */
	private String staticPin;
}
