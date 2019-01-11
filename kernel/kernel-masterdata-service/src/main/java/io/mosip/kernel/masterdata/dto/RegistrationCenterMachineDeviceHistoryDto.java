package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
/**
 * DTO class
 * @author Srinivasan
 *
 */
@Data
public class RegistrationCenterMachineDeviceHistoryDto  {

	@NotBlank
	@Size(min = 1, max = 10)
	private String regCenterId;
	
	@NotBlank
	@Size(min = 1, max = 10)
	private String machineId;
	
	@NotBlank
	@Size(min = 1, max = 36)
	private String deviceId;
	
	private LocalDateTime effectiveDateTime;
	
	private Boolean isActive;
}
