package io.mosip.kernel.masterdata.dto.getresponse.extn;

import io.mosip.kernel.masterdata.validator.FilterType;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response dto for Machine History Detail
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Machine Type", description = "Machine Type resource")
public class MachineTypeExtnDto extends BaseDto {

	@FilterType(types = { FilterTypeEnum.EQUALS })
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;

	@FilterType(types = { FilterTypeEnum.EQUALS })
	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;

	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

	@ApiModelProperty(value = "description", required = true, dataType = "java.lang.String")
	private String description;

}