package io.mosip.authentication.core.indauth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class For KycExchangeResponseDTO extending {@link BaseAuthResponseDTO}
 * 
 * @author Mahammed Taheer
 */

@Data
@EqualsAndHashCode(callSuper=true)
public class KycExchangeResponseDTO extends BaseAuthResponseDTO {
	
	/** The KycResponseDTO */
	private EncryptedKycRespDTO response;
	
}
