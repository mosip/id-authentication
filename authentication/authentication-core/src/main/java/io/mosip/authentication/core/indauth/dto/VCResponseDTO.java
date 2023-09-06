package io.mosip.authentication.core.indauth.dto;

import lombok.Data;

/**
 * The class for VCResponseDTO Holds the values for Verifiable Credential response data.
 * 
 * @author Mahammed Taheer
 *
 */

@Data
public class VCResponseDTO<T> {

	/** The Variable to hold value of Verifiable Credentials data */
	private T verifiableCredentials;
	
}
