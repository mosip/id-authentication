package io.mosip.registration.processor.status.sync.response.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestResponseDTO;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.status.dto.SyncErrorDTO;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new packet receiver response DTO.
 * @author Rishabh Keshari
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegSyncResponseDTO extends BaseRestResponseDTO {
	
	/** The response. */
	private List<SyncResponseDto> response;
	
	/** The error. */
	private List<SyncErrorDTO> errors;
	
}
