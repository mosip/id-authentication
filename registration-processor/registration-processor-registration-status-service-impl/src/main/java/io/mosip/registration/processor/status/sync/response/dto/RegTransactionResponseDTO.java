package io.mosip.registration.processor.status.sync.response.dto;

import java.util.List;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestResponseDTO;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.status.dto.RegistrationTransactionDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegTransactionResponseDTO extends BaseRestResponseDTO{
	/** The response. */
	private List<RegistrationTransactionDto> response;
	
	/** The error. */
	private List<ErrorDTO> errors;
}
