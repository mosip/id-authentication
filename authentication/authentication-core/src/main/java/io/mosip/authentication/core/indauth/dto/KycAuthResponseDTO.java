package io.mosip.authentication.core.indauth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class For KycAuthResponseDTO extending {@link BaseAuthResponseDTO}
 * 
 * @author Mahammed Taheer
 */

@Data
@EqualsAndHashCode(callSuper=true)
public class KycAuthResponseDTO extends BaseAuthResponseDTO {
	
	/** The KycResponseDTO */
	private KycAuthRespDTO response;
	
}
