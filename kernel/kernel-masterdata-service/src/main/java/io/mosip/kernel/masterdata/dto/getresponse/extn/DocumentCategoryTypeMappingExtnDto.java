package io.mosip.kernel.masterdata.dto.getresponse.extn;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DocumentCategoryTypeMapping", description = "DocumentCategoryTypeMapping resource representation")
public class DocumentCategoryTypeMappingExtnDto {

	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;

	@ApiModelProperty(value = "Application description", required = false, dataType = "java.lang.String")
	private String description;

	private DocumentTypeExtnDto documentType;

	@ApiModelProperty(value = "isActive", required = true, dataType = "java.lang.Boolean")
	private Boolean isActive;

	@ApiModelProperty(value = "Language Code", required = true, dataType = "java.lang.String")
	private String langCode;

	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

}