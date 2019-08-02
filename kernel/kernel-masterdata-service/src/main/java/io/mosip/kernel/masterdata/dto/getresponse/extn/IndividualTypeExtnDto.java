package io.mosip.kernel.masterdata.dto.getresponse.extn;

import io.mosip.kernel.masterdata.validator.FilterType;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Individual Types", description = "Individual Type details")
public class IndividualTypeExtnDto extends BaseDto {

	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;

	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;

	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

}