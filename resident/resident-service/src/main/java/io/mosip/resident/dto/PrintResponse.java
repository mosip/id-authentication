package io.mosip.resident.dto;

import java.util.List;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PrintResponse extends BaseResponseDTO {
	private static final long serialVersionUID = 1L;

	private List<ErrorDTO> errors;

    private UINCardResponseDTO response;
}
