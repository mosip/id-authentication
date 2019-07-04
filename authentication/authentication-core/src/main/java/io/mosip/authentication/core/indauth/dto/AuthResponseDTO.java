package io.mosip.authentication.core.indauth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@code AuthResponseDTO} is used for collect response from
 * core-kernel.Core-kernel get request from {@code AuthRequestDTO} and perform
 * operation.
 * 
 * 
 * 
 * @author Dinesh Karuppiah.T
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthResponseDTO extends BaseAuthResponseDTO {

	/** The Variable to hold response */
	private ResponseDTO response;
}
