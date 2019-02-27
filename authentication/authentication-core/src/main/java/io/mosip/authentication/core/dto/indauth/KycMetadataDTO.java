package io.mosip.authentication.core.dto.indauth;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 */
@Data
public class KycMetadataDTO {
	/**
	 * Boolean for consentReq
	 */
	private boolean consentRequired;
	/**
	 * Boolean for secLangReq
	 */
	private boolean secondaryLangCode;
}
