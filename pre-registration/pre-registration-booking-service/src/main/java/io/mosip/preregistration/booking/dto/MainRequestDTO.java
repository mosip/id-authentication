package io.mosip.preregistration.booking.dto;


import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MainRequestDTO<T> {
	@ApiModelProperty(value = "request id", position = 1)
	private String id;
	@ApiModelProperty(value = "request ver", position = 2)
	private String ver;
	@ApiModelProperty(value = "request teme", position = 3)
	private Date reqTime;
	@ApiModelProperty(value = "request", position = 4)
	private T request;

}
