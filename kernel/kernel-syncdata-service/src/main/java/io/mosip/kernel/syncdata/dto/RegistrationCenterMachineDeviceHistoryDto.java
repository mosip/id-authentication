package io.mosip.kernel.syncdata.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO class
 * 
 * @author Srinivasan
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegistrationCenterMachineDeviceHistoryDto extends BaseDto {

	@NotBlank
	@Size(min = 1, max = 10)
	private String regCenterId;

	@NotBlank
	@Size(min = 1, max = 10)
	private String machineId;

	@NotBlank
	@Size(min = 1, max = 36)
	private String deviceId;

	private LocalDateTime effectivetimes;

}
