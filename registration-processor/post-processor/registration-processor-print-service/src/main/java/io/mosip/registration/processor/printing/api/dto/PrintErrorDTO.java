package io.mosip.registration.processor.printing.api.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonPropertyOrder({ "registrationId", "status", "message", "errorcode" })
@Data
@EqualsAndHashCode(callSuper = false)
public class PrintErrorDTO extends ErrorDTO {

	public PrintErrorDTO(String errorcode, String message) {
		super(errorcode, message);
	}

	private static final long serialVersionUID = -5261464773892046294L;

	/** The registration id. */
	private String registrationId;

	/** The status. */
	private String status;

}
