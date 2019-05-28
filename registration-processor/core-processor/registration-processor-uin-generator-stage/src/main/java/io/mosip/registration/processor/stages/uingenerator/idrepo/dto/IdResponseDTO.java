package io.mosip.registration.processor.stages.uingenerator.idrepo.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
/**
 * The Class IdResponseDTO.
 *
 * @author M1049387
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@JsonFilter("responseFilter")
public class IdResponseDTO extends BaseIdRequestResponseDTO {
	
	/** The err. */
	private List<ErrorDTO> errors;
	
	private Object metadata;
	
	/** The response. */
	@JsonFilter("responseFilter")
	private ResponseDTO response;
}
