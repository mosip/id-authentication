package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RegistrationCenterMachineDeviceHistoryDto  {

	private String regCenterId;
	
	private String machineId;
	
	private String deviceId;
	
	private LocalDateTime effectiveDateTime;
	
	private Boolean isActive;
}
