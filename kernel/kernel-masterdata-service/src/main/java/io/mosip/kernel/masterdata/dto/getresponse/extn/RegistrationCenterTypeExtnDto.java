package io.mosip.kernel.masterdata.dto.getresponse.extn;

import io.mosip.kernel.masterdata.entity.RegistrationCenterType;
import io.mosip.kernel.masterdata.validator.FilterType;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO class for {@link RegistrationCenterType}.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Registration Center Types", description = "Registration center types")
public class RegistrationCenterTypeExtnDto extends BaseDto {

	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;

	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;

	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.CONTAINS, FilterTypeEnum.STARTSWITH })
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

	@ApiModelProperty(value = "descr", required = true, dataType = "java.lang.String")
	private String descr;
}
