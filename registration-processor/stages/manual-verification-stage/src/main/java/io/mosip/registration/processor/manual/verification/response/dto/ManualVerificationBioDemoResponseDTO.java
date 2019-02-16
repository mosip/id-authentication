package io.mosip.registration.processor.manual.verification.response.dto;

import io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new manual verification bio demo response DTO.
 * @author Rishabh Keshari
 */
@Data

/* (non-Javadoc)
 * @see io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO#hashCode()
 */
@EqualsAndHashCode(callSuper = true)
public class ManualVerificationBioDemoResponseDTO extends BaseRequestResponseDTO {
	
	/** The response. */
	private String file;
	
	/** The error. */
	private ManualVerificationErrorDTO error;
	
}
