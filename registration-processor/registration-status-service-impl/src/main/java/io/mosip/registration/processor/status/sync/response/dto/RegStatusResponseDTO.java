package io.mosip.registration.processor.status.sync.response.dto;

import java.util.List;

import io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new packet receiver response DTO.
 * @author Rishabh Keshari
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegStatusResponseDTO extends BaseRequestResponseDTO {
	
	/** The response. */
	private List<RegistrationStatusDto> response;
	
	/** The error. */
	private RegStatusErrorDTO error;
	
}
