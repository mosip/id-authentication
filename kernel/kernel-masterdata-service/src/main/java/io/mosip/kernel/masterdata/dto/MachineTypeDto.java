package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
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
	
	@NotNull
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;

	@NotNull
	@Size(min = 1, max = 3)
	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;

	@NotNull
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

	@Size(min = 1, max = 128)
	@ApiModelProperty(value = "description", required = true, dataType = "java.lang.String")
	private String description;
	
	@NotNull
	@ApiModelProperty(value = "isActive", required = true, dataType = "java.lang.Boolean")
	private Boolean isActive;

}
