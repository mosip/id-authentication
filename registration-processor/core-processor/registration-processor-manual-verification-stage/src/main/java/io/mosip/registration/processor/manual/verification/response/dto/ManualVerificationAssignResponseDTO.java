package io.mosip.registration.processor.manual.verification.response.dto;

import java.util.List;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestResponseDTO;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
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
public class ManualVerificationAssignResponseDTO extends BaseRestResponseDTO {

	private static final long serialVersionUID = -3476502854088317800L;

	/** The response. */
	private ManualVerificationDTO response;
	
	/** The error. */
	private List<ErrorDTO> errors;
	
}
