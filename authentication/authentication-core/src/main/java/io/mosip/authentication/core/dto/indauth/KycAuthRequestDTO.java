package io.mosip.authentication.core.dto.indauth;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * The Class For KycAuthRequestDTO extending BaseAuthRequestDTO
 * 
 * @author Prem Kumar
 * 
 */
@Data
public class KycAuthRequestDTO extends AuthRequestDTO {

	/** The value for secondary language code. */
	private String secondaryLangCode;

	/** The value for allowed Kyc Attributes. */
	@ApiModelProperty(required = false, hidden = true)
	private List<String> allowedKycAttributes;

}
