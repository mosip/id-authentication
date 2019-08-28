package io.mosip.authentication.demo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class AuthRequestDTO.
 * 
 * @author Sanjay Murali
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRequestDTO extends BaseAuthRequestDTO {

	/** The value for requestedAuth*/
	private AuthTypeDTO requestedAuth;

	/** The value for transactionID*/
	private String transactionID;

	/** The value for requestTime*/
	private String requestTime;
	
	/** The value for request*/
	private RequestDTO request;

	/** The value for consentObtained*/
	private boolean consentObtained;
	

	/** The value for individualId*/
	private String individualId;
	

	/** The value for individualIdType*/
	private String individualIdType;
	
	/** The value for requestHMAC*/
	private String requestHMAC;
	
	/** The value for keyIndex*/
	private String keyIndex;
	
	/** The value for requestSessionKey*/
	private String requestSessionKey;
	
	

}
