package io.mosip.authentication.core.indauth.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @author Dinesh Karuppiah.T
 * 
 *         RequestDto class
 * 
 */
@Data
public class RequestDTO {

	/** variable to hold otp value */
	private String otp;

	/** variable to hold staticPin value */
	@ApiModelProperty(required = false, hidden = true)
	private String staticPin;

	/** variable to hold timestamp value */
	private String timestamp;

	/** variable to hold identity value */
	private IdentityDTO demographics;

	/** List of biometric identity info */
	private List<BioIdentityInfoDTO> biometrics;

}
