package io.mosip.registration.processor.core.idrepo.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class IdResponseDTO.
 *
 * @author M1048358 Alok
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonFilter("responseFilter")
public class IdResponseDTO extends BaseIdRequestResponseDTO {

	/** The err. */
	private List<ErrorDTO> error;

	private Object metadata;

	/** The response. */
	@JsonFilter("responseFilter")
	private ResponseDTO response;
}
