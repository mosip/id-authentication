package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Response dto for Biometric Type Detail
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@Data
@ApiModel(value = "BiometricType", description = "BiometricType resource representation")
public class BiometricTypeDto {

	@NotNull
	@Size(min = 1, max = 3)
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;

	@NotNull
	@Size(min = 1, max = 64)
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

	@Size(min = 0, max = 128)
	@ApiModelProperty(value = "Application description", required = false, dataType = "java.lang.String")
	private String description;

	@NotNull
	@Size(min = 1, max = 3)
	@ApiModelProperty(value = "Language Code", required = true, dataType = "java.lang.String")
	private String langCode;
	
	@NotNull
	@ApiModelProperty(value = "Application isActive Status", required =  true, dataType = "java.lang.Boolean")
	private Boolean isActive;

}
