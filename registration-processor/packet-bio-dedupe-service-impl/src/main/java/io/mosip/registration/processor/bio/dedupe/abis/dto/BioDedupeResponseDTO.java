package io.mosip.registration.processor.bio.dedupe.abis.dto;

import io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new bio dedupe response DTO.
 * @author Rishabh Keshari
 */
@Data

/* (non-Javadoc)
 * @see io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO#hashCode()
 */
@EqualsAndHashCode(callSuper = true)
public class BioDedupeResponseDTO extends BaseRequestResponseDTO {
	
	/** The response. */
	private String file;
	
	/** The error. */
	private BioDedupeErrorDTO error;
	
}
