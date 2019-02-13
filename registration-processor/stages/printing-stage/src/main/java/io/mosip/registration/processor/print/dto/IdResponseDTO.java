package io.mosip.registration.processor.print.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

import io.mosip.registration.processor.core.packet.dto.regcentermachine.ErrorDTO;
import lombok.Data;

/**
 * The Class IdResponseDTO.
 *
 * @author M1048358 Alok
 */
@Data
public class IdResponseDTO {

	/** The err. */
	private List<ErrorDTO> error;

	/** The status. */
	private String status;

	/** The response. */
	@JsonFilter("responseFilter")
	private ResponseDTO response;
}
