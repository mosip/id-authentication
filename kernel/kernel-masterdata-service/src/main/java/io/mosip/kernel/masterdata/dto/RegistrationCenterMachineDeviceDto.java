package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.mosip.kernel.masterdata.validator.ValidLangCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Data

public class RegistrationCenterMachineDeviceDto {

	@NotBlank
	@Size(min = 1, max = 10)
	private String regCenterId;

	@NotBlank
	@Size(min = 1, max = 10)
	private String machineId;

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
