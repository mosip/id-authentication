package io.mosip.kernel.syncdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationCenterMachineDeviceDto extends BaseDto{

	@NotNull
	@Size(min = 1, max = 36)
	private String regCenterId;

	@NotNull
	@Size(min = 1, max = 36)
	private String machineId;

	@NotNull
	@Size(min = 1, max = 36)
	private String deviceId;

	@NotNull
	private Boolean isActive;
}
