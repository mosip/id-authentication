package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Response dto for Machine History Detail
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Data
@ApiModel(value = "Machine Type", description = "Machine Type resource")
public class MachineTypeDto {

	@NotBlank
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;

	@NotBlank
	@Size(min = 1, max = 3)
	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;

	@NotBlank
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

	@Size(min = 1, max = 128)
	@ApiModelProperty(value = "description", required = true, dataType = "java.lang.String")
	private String description;

	@NotBlank
	@ApiModelProperty(value = "isActive", required = true, dataType = "java.lang.Boolean")
	private Boolean isActive;

}
