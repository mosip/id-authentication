package io.mosip.authentication.core.dto.indauth;

import lombok.Data;

/**
 * The Class for KycInfoDTO
 * 
 * @author Prem Kumar
 *
 *
 */
@Data
public class KycInfoDTO {

	/** The IdentityInfoDTO */
	private IdentityInfoDTO identity;

	/** The String value for ePrint */
	private String ePrint;

}
