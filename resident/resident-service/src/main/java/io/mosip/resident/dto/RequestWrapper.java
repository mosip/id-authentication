package io.mosip.resident.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class RequestWrapper<T> {
	private String id;
	private String version;
	private String requesttime;

	@NotNull(message = "request should not be null")
	@Valid
	private T request;
}
