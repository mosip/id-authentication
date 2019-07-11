package io.mosip.registration.processor.manual.verification.request.dto;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new manual verification decision request DTO.
 * @author Rishabh Keshari
 */
@Data

/* (non-Javadoc)
 * @see io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO#hashCode()
 */
@EqualsAndHashCode(callSuper = true)
public class ManualVerificationDecisionRequestDTO extends BaseRestRequestDTO {
	
	private static final long serialVersionUID = -8968744480450135803L;
	/** The request. */
	private ManualVerificationDTO request;
		
}
