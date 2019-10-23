package io.mosip.kernel.core.http;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RequestWrapper<T> {
	private String id;
	private String version;
	@ApiModelProperty(notes = "Request Timestamp", example = "2018-12-10T06:12:52.994Z", required = true)
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime requesttime;

	private Object metadata;

	@NotNull
	@Valid
	private T request;
}
