package io.mosip.kernel.masterdata.dto.getresponse.extn;

import io.mosip.kernel.masterdata.validator.FilterType;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response dto for Document Category Detail
 * 
 * @author Neha Sinha
 * @author Ritesh Sinha
 * @author Uday Kumar
 * 
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DocumentCategory", description = "DocumentCategory resource representation")
public class DocumentCategoryExtnDto extends BaseDto {

	@FilterType(types = { FilterTypeEnum.EQUALS})
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;

	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

	@ApiModelProperty(value = "Application description", required = false, dataType = "java.lang.String")
	private String description;

	@FilterType(types = { FilterTypeEnum.EQUALS })
	@ApiModelProperty(value = "Language Code", required = true, dataType = "java.lang.String")
	private String langCode;

}