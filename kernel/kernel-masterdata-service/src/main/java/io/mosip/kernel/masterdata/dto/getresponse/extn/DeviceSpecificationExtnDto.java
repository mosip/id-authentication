package io.mosip.kernel.masterdata.dto.getresponse.extn;

import io.mosip.kernel.masterdata.validator.FilterType;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceSpeicification", description = "DeviceSpecification Detail resource")
public class DeviceSpecificationExtnDto extends BaseDto {

	@FilterType(types = { FilterTypeEnum.EQUALS })
	@ApiModelProperty(value = "id", required = true, dataType = "java.lang.String")
	private String id;

	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	@ApiModelProperty(value = "brand", required = true, dataType = "java.lang.String")
	private String brand;

	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	@ApiModelProperty(value = "model", required = true, dataType = "java.lang.String")
	private String model;

	@ApiModelProperty(value = "deviceTypeCode", required = true, dataType = "java.lang.String")
	private String deviceTypeCode;

	@ApiModelProperty(value = "minDriverversion", required = true, dataType = "java.lang.String")
	private String minDriverversion;

	@ApiModelProperty(value = "description", required = true, dataType = "java.lang.String")
	private String description;

	@FilterType(types = { FilterTypeEnum.EQUALS })
	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;

}