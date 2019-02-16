package io.mosip.registration.processor.bio.dedupe.abis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data

/**
 * Instantiates a new bio dedupe error DTO.
 *
 * @param errorcode the errorcode
 * @param message the message
 * @author Rishabh Keshari
 */
@AllArgsConstructor
public class BioDedupeErrorDTO {

	/** The errorcode. */
	private String errorcode;
	
	/** The message. */
	private String message;
}
