package io.mosip.kernel.masterdata.dto.response;

import io.mosip.kernel.masterdata.dto.getresponse.extn.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class LocationSearchDto extends BaseDto {

	@ApiModelProperty(value = "region", required = true, dataType = "java.lang.String")
	private String region;

	@ApiModelProperty(value = "province", required = true, dataType = "java.lang.String")
	private String province;

	@ApiModelProperty(value = "city", required = true, dataType = "java.lang.String")
	private String city;

	@ApiModelProperty(value = "zone", required = true, dataType = "java.lang.String")
	private String zone;

	@ApiModelProperty(value = "postalCode", required = true, dataType = "java.lang.String")
	private String postalCode;

}
