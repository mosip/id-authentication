package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.mosip.kernel.masterdata.validator.ValidLangCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Data
@ApiModel(description = "Model representing a Registration-Center-Device-Mapping Request")
public class RegistrationCenterDeviceDto {

	@NotBlank
	@Size(min = 1, max = 10)
	private String regCenterId;

	@NotBlank
	@Size(min = 1, max = 36)
	private String deviceId;

	@NotNull
	private Boolean isActive;

	@NotBlank
	@Size(min = 1, max = 3)
	@ValidLangCode
	@ApiModelProperty(value = "Language code", required = true, dataType = "java.lang.String")
	private String langCode;

}
