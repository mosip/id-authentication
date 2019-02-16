package io.mosip.registration.processor.manual.verification.request.dto;

import io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO;
import io.mosip.registration.processor.manual.verification.dto.PacketInfoRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new manual app demographic request DTO.
 * @author Rishabh Keshari
 */
@Data

/* (non-Javadoc)
 * @see io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO#hashCode()
 */
@EqualsAndHashCode(callSuper = true)
public class ManualAppDemographicRequestDTO extends BaseRequestResponseDTO {
	
	/** The request. */
	private PacketInfoRequestDto request;
		
}
