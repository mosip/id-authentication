package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Data
@ApiModel(description = "Model representing a Registration-Center-Machine-Mapping Request")
public class RegistrationCenterMachineDto {

	@NotBlank
	@Size(min = 1, max = 10)
	@ApiModelProperty(notes = "Registration Center Id for request", example = "RC001", required = true)
	private String regCenterId;

	@NotBlank
	@Size(min = 1, max = 10)
	@ApiModelProperty(notes = "Machine Id for request", example = "MC001", required = true)
	private String machineId;

	@NotNull
	@ApiModelProperty(notes = "mapping is active or not", required = true)
	private Boolean isActive;
}
