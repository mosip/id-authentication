package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Class for Device Type DTO
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@Data
public class DeviceTypeDto {

	@NotNull
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;

	@NotNull
	@Size(min = 1, max = 3)
	@ApiModelProperty(value = "langCodes", required = true, dataType = "java.lang.String")
	private String langCode;

	
	@NotNull
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

	@Size(min = 1, max = 128)
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String description;
	
	@NotNull
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.Boolean")
	private Boolean isActive;
}
