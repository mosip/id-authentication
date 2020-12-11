package io.mosip.authentication.core.indauth.dto;

import java.util.Map;

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
public class KycAuthResponseDTO extends BaseAuthResponseDTO {
	
	/** The KycResponseDTO */
	private KycResponseDTO response;
	
	private Map<String, Object> metadata;

}
