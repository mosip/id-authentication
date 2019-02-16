package io.mosip.registration.processor.bio.dedupe.abis.dto;

import io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new bio dedupe request DTO.
 * @author Rishabh Keshari
 */
@Data

/* (non-Javadoc)
 * @see io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO#hashCode()
 */
@EqualsAndHashCode(callSuper = true)
public class BioDedupeRequestDTO extends BaseRequestResponseDTO {
	
	/** The request. */
	private BioDedupeRegIdDto request;
		
}
