package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Data


public class RegistrationCenterMachineDto {

	@NotNull
	@Size(min = 1, max = 36)
	private String regCenterId;

	@NotNull
	@Size(min = 1, max = 36)
	private String machineId;

	@NotNull
	private Boolean isActive;
}
