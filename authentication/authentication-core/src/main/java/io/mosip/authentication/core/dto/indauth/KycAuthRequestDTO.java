package io.mosip.authentication.core.dto.indauth;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 *The Class For KycAuthRequestDTO extending BaseAuthRequestDTO
 */
@Data
public class KycAuthRequestDTO extends BaseAuthRequestDTO {
	
	/**
	 * Boolean for consentReq
	 */
	private boolean consentReq;
	/**
	 * Boolean for ePrintReq
	 */
	private boolean ePrintReq;
	/**
	 * Boolean for secLangReq
	 */
	private boolean secLangReq;
	
	/** String for  eKycAuthType*/
	private String eKycAuthType;
	
	/** The AuthRequestDTO */
	private AuthRequestDTO authRequest;

}
