package io.mosip.authentication.core.dto.indauth;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 *The Class for KycInfoDTO
 */
@Data
public class KycInfoDTO {
	
		/** The IdentityInfoDTO */
		private IdentityInfoDTO identity;
		
		/** The String value for ePrint */
		private String ePrint;
		
}
