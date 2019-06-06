package io.mosip.authentication.core.indauth.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * General-purpose of {@code RequestDTO} class used to provide Request Info's
 * 
 * @author Dinesh Karuppiah.T
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
