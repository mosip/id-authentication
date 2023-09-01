package io.mosip.authentication.core.indauth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class For VciExchangeResponseDTO extending {@link BaseAuthResponseDTO}
 * 
 * @author Mahammed Taheer
 */

@Data
@EqualsAndHashCode(callSuper=true)
public class VciExchangeResponseDTO extends BaseAuthResponseDTO {
	
	/** The VCResponseDTO */
	private VCResponseDTO<?> response;
	
}
