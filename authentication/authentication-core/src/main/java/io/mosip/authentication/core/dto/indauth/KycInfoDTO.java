package io.mosip.authentication.core.dto.indauth;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 */
@Data
public class KycInfoDTO {
	
	
		private IdentityInfoDTO identity;
		
		private String ePrint;
		
}
