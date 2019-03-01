package io.mosip.authentication.core.dto.indauth;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Prem Kumar
 *
 *The Class For KycAuthResponseDTO extending BaseAuthResponseDTO
 */

@Data
@EqualsAndHashCode(callSuper=true	)
public class KycAuthResponseDTO extends AuthResponseDTO {
	
	/** The KycResponseDTO */
	private KycResponseDTO response;

}
