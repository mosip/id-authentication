package io.mosip.authentication.core.dto.idrepo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;

import io.mosip.authentication.core.dto.indauth.BaseAuthRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class IdResponseDTO.
 *
 * @author Rakesh Roshan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonFilter("responseFilter")
public class IdResponseDTO extends BaseAuthRequestDTO {

	/** The timestamp. */
	private String timestamp;

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