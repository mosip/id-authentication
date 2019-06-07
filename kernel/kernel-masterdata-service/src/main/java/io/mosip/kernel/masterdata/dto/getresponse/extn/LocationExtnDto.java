package io.mosip.kernel.masterdata.dto.getresponse.extn;

import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.mosip.kernel.masterdata.validator.CustomIntegerDeserializer;
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

	@ApiModelProperty(value = "name", required = true, dataType = "java.lang.String")
	private String name;

	@JsonDeserialize(using = CustomIntegerDeserializer.class)
	@Range(min = 0)
	@ApiModelProperty(value = "hierarchyLevel", required = true, dataType = "java.lang.Integer")
	private int hierarchyLevel;

	@ApiModelProperty(value = "hierarchyName", required = true, dataType = "java.lang.String")
	private String hierarchyName;

	@ApiModelProperty(value = "parentLocCode", required = true, dataType = "java.lang.String")
	private String parentLocCode;

	@ApiModelProperty(value = "langCode", required = true, dataType = "java.lang.String")
	private String langCode;

}
