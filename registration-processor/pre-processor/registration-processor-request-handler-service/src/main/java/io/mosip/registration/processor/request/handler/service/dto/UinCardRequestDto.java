package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UinCardRequestDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8099293922889142622L;

	@NotNull(message = "centerId should not be null ")
	@NotBlank(message = "centerId should not be empty")
	private String centerId;
	@NotNull(message = "machineId should not be null ")
	@NotBlank(message = "machineId should not be empty")
	private String machineId;
	@NotNull(message = "reason should not be null ")
	@NotBlank(message = "reason should not be empty")
	private String reason;
	@NotNull(message = "registrationType should not be null ")
	@NotBlank(message = "registrationType should not be empty")
	private String registrationType;
	@NotNull(message = "id should not be null ")
	@NotBlank(message = "id should not be empty")
	private String id;
	@NotNull(message = "idType should not be null ")
	@NotBlank(message = "idType should not be empty")
	private String idType;
	@NotNull(message = "cardType should not be null ")
	@NotBlank(message = "cardType should not be empty")
	private String cardType;
}
