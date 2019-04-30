package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;
import java.util.List;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestResponseDTO;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Instantiates a new packet receiver response DTO.
 * 
 * @author Rishabh Keshari
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegSyncResponseDTO extends BaseRestResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5205090798016497816L;

	/** The response. */
	private List<SyncResponseDto> response;

	/** The error. */
	private List<ErrorDTO> errors;

}
