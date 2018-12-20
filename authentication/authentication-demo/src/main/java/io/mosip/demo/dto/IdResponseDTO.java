package io.mosip.demo.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class IdResponseDTO.
 *
 * @author Manoj SP
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonFilter("responseFilter")
public class IdResponseDTO extends BaseIdRequestResponseDTO {
	
	/** The err. */
	private List<ErrorDTO> err;
	
	/** The registration id. */
	private String registrationId;
	
	/** The status. */
	private String status;
	
	/** The response. */
	@JsonFilter("responseFilter")
	private ResponseDTO response;
}
