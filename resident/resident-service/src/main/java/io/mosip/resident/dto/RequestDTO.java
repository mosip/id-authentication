package io.mosip.resident.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class RequestDTO {
	@NotBlank
	private String individualId;
	@NotBlank
	private String individualIdType;

}