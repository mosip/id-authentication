package io.mosip.kernel.masterdata.dto.getresponse.extn;

import io.mosip.kernel.masterdata.validator.FilterType;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class for Device Type DTO
 * 
 * @author Megha Tanga
 * @author Ayush Saxena
 * @since 1.0.0
 *
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Device Type", description = "Device Type Detail resource")
public class DeviceTypeExtnDto extends BaseDto {

	/**
	 * Field for device type code
	 */
	@FilterType(types = { FilterTypeEnum.EQUALS })
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;

	/**
	 * Field for language code
	 */
	@FilterType(types = { FilterTypeEnum.EQUALS })
	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;

	/**
	 * Field for device type name
	 */
	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

	/**
	 * Field for description
	 */
	@ApiModelProperty(value = "description", required = true, dataType = "java.lang.String")
	private String description;
}