package io.mosip.authentication.core.indauth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class For IdentityKeyBindingRequestDTO extending BaseAuthRequestDTO
 * 
 * @author Mahammed Taheer
 * 
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class IdentityKeyBindingRequestDTO extends AuthRequestDTO {
	
	/** The value for Identity public key. */
	private IdentityKeyBindingDTO identityKeyBinding;

}
