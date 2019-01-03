package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.mosip.kernel.masterdata.validator.ValidLangCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TemplateFileFormat", description = "TemplateFileFormat resource representation")
public class TemplateFileFormatDto {
	
	@NotNull
	@Size(min = 1, max = 36)
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;
	
	@Size(min = 0, max = 256)
	@ApiModelProperty(value = "TemplateFileFormat description", required = false, dataType = "java.lang.String")
	private String description;
	
	// @ValidLangCode
	@NotNull
	@Size(min = 1, max = 3)
	@ApiModelProperty(value = "Language code", required = true, dataType = "java.lang.String")
	private String langCode;
	
	@NotNull
	@ApiModelProperty(value = "TemplateFileFormat isActive status", required = true, dataType = "java.lang.Boolean")
	private Boolean isActive;
}
