package io.mosip.registration.processor.manual.verification.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Instantiates a new error DTO.
 *
 * @author Rishabh Keshari
 */

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data

/**
 * Instantiates a new manual verification error DTO.
 *
 * @param errorcode the errorcode
 * @param message the message
 */
@AllArgsConstructor
public class ManualVerificationErrorDTO {

	/** The errorcode. */
	private String errorcode;
	
	/** The message. */
	private String message;
}
