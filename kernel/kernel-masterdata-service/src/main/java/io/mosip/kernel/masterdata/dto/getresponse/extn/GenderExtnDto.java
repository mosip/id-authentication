package io.mosip.kernel.masterdata.dto.getresponse.extn;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * GenderType Dto for request
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Gender types", description = "Model representing a Request")
public class GenderExtnDto extends BaseDto {

	@ApiModelProperty(notes = "Code of gender ", example = "GC001", required = true)
	private String code;

	@ApiModelProperty(notes = "Name of the Gender", example = "Male", required = true)
	private String genderName;

	@ApiModelProperty(notes = "Language Code", example = "ENG", required = true)
	private String langCode;

}
