package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class RegistrationCenterMachineDto {
	private String regId;	
	private String machineId;
	private String regcntrId;
	private Boolean isActive;
	private String latitude;
	private String longitude;
}
