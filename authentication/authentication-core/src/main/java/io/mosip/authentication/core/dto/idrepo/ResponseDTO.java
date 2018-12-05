package io.mosip.authentication.core.dto.idrepo;

import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import lombok.Data;

/**
 * The Class ResponseDTO.
 *
 * @author Rakesh Roshan
 */
@Data
public class ResponseDTO {
	
	/** The entity. */
	private String entity;
	
	/** The identity. */
	//private Object identity;
	private IdentityDTO identity;
}
