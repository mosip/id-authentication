package io.mosip.authentication.core.dto.indauth;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
@Data
public class RequestDTO {
	
	/** variable to hold otp value */
	private String otp;
	
	/** variable to hold staticPin value */
	private String staticPin;
	
	/** variable to hold timestamp value */
	private String timestamp;
	
	/** variable to hold transactionID value */
	private String transactionID;

	/** variable to hold identity value */
	private IdentityDTO demographics;
	
	/** List of biometric identity info */
	private List<BioIdentityInfoDTO> biometrics;

}
