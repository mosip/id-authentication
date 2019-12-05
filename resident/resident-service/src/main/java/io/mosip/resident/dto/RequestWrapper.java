package io.mosip.resident.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class RequestWrapper<T> {
	private String id;
	private String version;
	private String requesttime;

	@NotNull
	@Valid
	private T request;
}
