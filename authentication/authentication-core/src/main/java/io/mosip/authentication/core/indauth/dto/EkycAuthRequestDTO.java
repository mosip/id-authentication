package io.mosip.authentication.core.indauth.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class For KycAuthRequestDTO extending BaseAuthRequestDTO
 * 
 * @author Prem Kumar
 * 
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class EkycAuthRequestDTO extends AuthRequestDTO {
	
	/** The value for allowed Kyc Attributes. */
	@ApiModelProperty(required = false, hidden = true)
	private List<String> allowedKycAttributes;

}
