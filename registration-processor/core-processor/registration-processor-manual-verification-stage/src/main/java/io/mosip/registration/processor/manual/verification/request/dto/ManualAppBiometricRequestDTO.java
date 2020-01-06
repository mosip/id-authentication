package io.mosip.registration.processor.manual.verification.request.dto;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO;
import io.mosip.registration.processor.manual.verification.dto.FileRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Instantiates a new manual app biometric request DTO.
 * @author Rishabh Keshari
 */
@Data

/* (non-Javadoc)
 * @see io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO#hashCode()
 */
@EqualsAndHashCode(callSuper = true)
public class ManualAppBiometricRequestDTO extends BaseRestRequestDTO {
	
	private static final long serialVersionUID = -1458603171977337964L;
	/** The request. */
	private FileRequestDto request;
		
}
