package io.mosip.kernel.masterdata.dto.getresponse.extn;

import io.mosip.kernel.masterdata.validator.FilterType;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO class for fetching titles from masterdata with all metadata
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Title", description = "Title details")
public class TitleExtnDto extends BaseDto {

	@FilterType(types = { FilterTypeEnum.EQUALS })
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;

	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	@ApiModelProperty(value = "titleName", required = true, dataType = "java.lang.String")
	private String titleName;

	@ApiModelProperty(value = "titleDescription", required = true, dataType = "java.lang.String")
	private String titleDescription;

	@FilterType(types = { FilterTypeEnum.EQUALS })
	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;

}