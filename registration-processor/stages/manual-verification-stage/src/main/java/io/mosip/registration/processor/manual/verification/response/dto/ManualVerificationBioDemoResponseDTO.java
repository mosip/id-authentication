package io.mosip.registration.processor.manual.verification.response.dto;

import java.util.List;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestResponseDTO;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
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
public class ManualVerificationBioDemoResponseDTO extends BaseRestResponseDTO {
	
	private static final long serialVersionUID = -8196265427738296193L;

	/** The response. */
	private String file;
	
	/** The error. */
	private List<ErrorDTO> errors;
	
}
