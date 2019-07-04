package io.mosip.kernel.masterdata.dto.getresponse.extn;

import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.mosip.kernel.masterdata.validator.CustomIntegerDeserializer;
import io.mosip.kernel.masterdata.validator.FilterType;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Location", description = "Location details")
public class LocationExtnDto extends BaseDto {
	@ApiModelProperty(value = "code", required = true, dataType = "java.lang.String")
	private String code;
	
	@FilterType(types= {FilterTypeEnum.EQUALS,FilterTypeEnum.CONTAINS,FilterTypeEnum.STARTSWITH})
	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;
	
	
	@FilterType(types= {FilterTypeEnum.EQUALS,FilterTypeEnum.CONTAINS,FilterTypeEnum.STARTSWITH})
	@JsonDeserialize(using = CustomIntegerDeserializer.class)
	@Range(min = 0)
	@ApiModelProperty(value = "hierarchyLevel", required = true, dataType = "java.lang.Integer")
	private int hierarchyLevel;

	@FilterType(types= {FilterTypeEnum.EQUALS,FilterTypeEnum.CONTAINS,FilterTypeEnum.STARTSWITH})
	@ApiModelProperty(value = "hierarchyName", required = true, dataType = "java.lang.String")
	private String hierarchyName;

	@ApiModelProperty(value = "parentLocCode", required = true, dataType = "java.lang.String")
	private String parentLocCode;

	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;

}
