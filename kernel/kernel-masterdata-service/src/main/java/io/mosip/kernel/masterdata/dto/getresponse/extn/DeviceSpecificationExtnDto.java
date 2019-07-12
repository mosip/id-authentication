package io.mosip.kernel.masterdata.dto.getresponse.extn;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceSpeicification", description = "DeviceSpecification Detail resource")
public class DeviceSpecificationExtnDto extends BaseDto {

	@ApiModelProperty(value = "id", required = true, dataType = "java.lang.String")
	private String id;

	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

	@ApiModelProperty(value = "brand", required = true, dataType = "java.lang.String")
	private String brand;

	@ApiModelProperty(value = "model", required = true, dataType = "java.lang.String")
	private String model;

	@ApiModelProperty(value = "deviceTypeCode", required = true, dataType = "java.lang.String")
	private String deviceTypeCode;

	@ApiModelProperty(value = "minDriverversion", required = true, dataType = "java.lang.String")
	private String minDriverversion;

	@ApiModelProperty(value = "description", required = true, dataType = "java.lang.String")
	private String description;

	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;

}