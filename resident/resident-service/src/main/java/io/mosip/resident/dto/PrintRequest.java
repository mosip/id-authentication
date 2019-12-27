package io.mosip.resident.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PrintRequest extends BaseRequestDTO {

	private static final long serialVersionUID = 1L;

	/** The request. */
	private UINCardRequestDTO request;
}
