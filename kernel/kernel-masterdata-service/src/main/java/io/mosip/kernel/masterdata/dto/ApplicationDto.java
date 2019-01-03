package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Application", description = "Application resource representation")
public class ApplicationDto {

	@NotBlank
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;

	@NotBlank
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

	@Size(min = 0, max = 128)
	@ApiModelProperty(value = "Application description", required = false, dataType = "java.lang.String")
	private String description;

	@NotBlank
	@Size(min = 1, max = 3)
	@ApiModelProperty(value = "Language Code", required = true, dataType = "java.lang.String")
	private String langCode;
	
	@NotBlank
	@ApiModelProperty(value = "Application isActive Status", required =  true, dataType = "java.lang.Boolean")
	private Boolean isActive;

}
