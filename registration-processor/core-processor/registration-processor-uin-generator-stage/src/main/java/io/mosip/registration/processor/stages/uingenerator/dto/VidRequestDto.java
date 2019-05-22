package io.mosip.registration.processor.stages.uingenerator.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
@ApiModel(description = "Model representing a Vid Request")
public class VidRequestDto {
	
	private String vidtype;
	
	private Long uin;

}

