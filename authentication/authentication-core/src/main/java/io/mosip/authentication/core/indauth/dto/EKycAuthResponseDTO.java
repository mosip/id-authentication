package io.mosip.authentication.core.indauth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class For KycAuthResponseDTO extending {@link AuthResponseDTO}
 * 
 * @author Prem Kumar
 *
 *
 */

@Data
@EqualsAndHashCode(callSuper=true	)
public class EKycAuthResponseDTO extends BaseAuthResponseDTO {
	
	/** The KycResponseDTO */
	private EKycResponseDTO response;
	
}
