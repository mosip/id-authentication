package io.mosip.registration.processor.manual.verification.response.dto;

import io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Instantiates a new manual verification assign response DTO.
 * @author Rishabh Keshari
 */
@Data

/* (non-Javadoc)
 * @see io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO#hashCode()
 */
@EqualsAndHashCode(callSuper = true)
public class ManualVerificationAssignResponseDTO extends BaseRequestResponseDTO {
	
	/** The response. */
	private ManualVerificationDTO response;
	
	/** The error. */
	private ManualVerificationErrorDTO error;
	
}
