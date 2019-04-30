package io.mosip.kernel.core.idrepo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class ResponseDTO.
 *
 * @author Manoj SP
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RequestDTO extends BaseRequestResponseDTO {

	/** The registration id. */
	private String registrationId;
}
