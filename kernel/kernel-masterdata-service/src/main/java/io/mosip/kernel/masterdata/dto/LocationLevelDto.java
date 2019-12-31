package io.mosip.kernel.masterdata.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "LocationList", description = "Location list for the country level")
public class LocationLevelDto {
	
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;
	
	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;
	
}
