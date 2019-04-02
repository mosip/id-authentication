package io.mosip.registration.processor.printing.api.dto;

import java.util.List;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestResponseDTO;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PrintResponse extends BaseRestResponseDTO {

	private static final long serialVersionUID = 1L;

	private List<ErrorDTO> errors;

    private ResponseDTO response;
}
