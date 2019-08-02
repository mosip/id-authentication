package io.mosip.kernel.masterdata.dto.getresponse.extn;

import io.mosip.kernel.masterdata.validator.FilterType;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Blacklisted word DTO.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Blacklisted word", description = "blacklisted words details")
public class BlacklistedWordsExtnDto extends BaseDto {

	@FilterType(types= {FilterTypeEnum.EQUALS,FilterTypeEnum.STARTSWITH,FilterTypeEnum.CONTAINS})
	@ApiModelProperty(value = "word", required = true, dataType = "java.lang.String")
	private String word;

	@FilterType(types= {FilterTypeEnum.EQUALS,FilterTypeEnum.STARTSWITH,FilterTypeEnum.CONTAINS})
	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;

	@ApiModelProperty(value = "description", required = true, dataType = "java.lang.String")
	private String description;
}