package io.mosip.kernel.masterdata.dto.getresponse.extn;

import io.mosip.kernel.masterdata.validator.FilterType;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response Dto for Template details
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Template", description = "Template resource representation")
public class TemplateExtnDto extends BaseDto {

	@FilterType(types = { FilterTypeEnum.EQUALS })
	@ApiModelProperty(value = "id", required = true, dataType = "java.lang.String")
	private String id;

	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

	@ApiModelProperty(value = "Template description", required = false, dataType = "java.lang.String")
	private String description;

	@ApiModelProperty(value = "Template file format code", required = true, dataType = "java.lang.String")
	private String fileFormatCode;

	@ApiModelProperty(value = "model", required = false, dataType = "java.lang.String")
	private String model;

	@ApiModelProperty(value = "file text", required = false, dataType = "java.lang.String")
	private String fileText;

	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	@ApiModelProperty(value = "module Id", required = false, dataType = "java.lang.String")
	private String moduleId;

	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	@ApiModelProperty(value = "module name", required = false, dataType = "java.lang.String")
	private String moduleName;

	@ApiModelProperty(value = "Template file format code", required = true, dataType = "java.lang.String")
	private String templateTypeCode;

	@FilterType(types = { FilterTypeEnum.EQUALS })
	@ApiModelProperty(value = "Language code", required = true, dataType = "java.lang.String")
	private String langCode;

}