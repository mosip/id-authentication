package io.mosip.authentication.core.indauth.dto;

import java.util.Map;

import io.mosip.authentication.core.dto.ObjectWithMetadata;
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
public class KycAuthResponseDTO extends BaseAuthResponseDTO implements ObjectWithMetadata {
	
	/** The KycResponseDTO */
	private KycResponseDTO response;
	
	private Map<String, Object> metadata;

}
