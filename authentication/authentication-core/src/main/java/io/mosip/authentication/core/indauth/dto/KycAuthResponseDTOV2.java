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
public class KycAuthResponseDTOV2 extends BaseAuthResponseDTO {
	
	/** The KycResponseDTO */
	private KycAuthRespDTOV2 response;
	
}
