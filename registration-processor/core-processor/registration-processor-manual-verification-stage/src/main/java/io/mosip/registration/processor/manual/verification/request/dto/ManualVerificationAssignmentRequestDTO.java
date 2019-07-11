package io.mosip.registration.processor.manual.verification.request.dto;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO;
import io.mosip.registration.processor.manual.verification.dto.UserDto;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Instantiates a new manual verification assignment request DTO.
 * @author Rishabh Keshari
 */
@Data

/* (non-Javadoc)
 * @see io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO#hashCode()
 */
@EqualsAndHashCode(callSuper = true)
public class ManualVerificationAssignmentRequestDTO extends BaseRestRequestDTO {
	
	private static final long serialVersionUID = -40776199886412084L;
	/** The request. */
	private UserDto request;

		
}
