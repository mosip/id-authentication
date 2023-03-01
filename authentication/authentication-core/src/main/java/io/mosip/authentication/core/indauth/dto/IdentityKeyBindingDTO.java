package io.mosip.authentication.core.indauth.dto;

import java.util.Map;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * The Class For IdentityKeyBindingRequestDTO extending BaseAuthRequestDTO
 * 
 * @author Mahammed Taheer
 * 
 */
@Data
public class IdentityKeyBindingDTO {
	
	/** The value for Identity public key JWK. */
	@ApiModelProperty(required = true)
	private Map<String, Object> publicKeyJWK;

	@ApiModelProperty(required = true)
	private String authFactorType;

}
