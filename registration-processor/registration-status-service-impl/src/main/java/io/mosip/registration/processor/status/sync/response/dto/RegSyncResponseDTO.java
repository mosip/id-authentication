package io.mosip.registration.processor.status.sync.response.dto;

import java.util.List;
import io.mosip.registration.processor.core.packet.dto.BaseRequestResponseDTO;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new packet receiver response DTO.
 * @author Rishabh Keshari
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegSyncResponseDTO extends BaseRequestResponseDTO {
	
	/** The response. */
	private List<SyncResponseDto> response;
	
	/** The error. */
	private RegStatusErrorDTO error;
	
}
