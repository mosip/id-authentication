package io.mosip.authentication.core.indauth.dto;

import java.util.Map;

import io.mosip.authentication.core.dto.ObjectWithIdVersionTransactionID;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
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
public class AuthResponseDTO extends BaseAuthResponseDTO implements ObjectWithMetadata, ObjectWithIdVersionTransactionID{

	/** The Variable to hold response */
	private ResponseDTO response;
	
	private Map<String, Object> metadata;

}
