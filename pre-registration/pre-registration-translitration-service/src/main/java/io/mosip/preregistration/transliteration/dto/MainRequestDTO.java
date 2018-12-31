package io.mosip.preregistration.transliteration.dto;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MainRequestDTO<T> {
	@ApiModelProperty(value = "request id", position = 1)
	private String id;
	@ApiModelProperty(value = "version", position = 2)
	private String ver;
	@ApiModelProperty(value = "request time", position = 3)
	private Date reqTime;
	@ApiModelProperty(value = "request", position = 4)
	private T request;

}
