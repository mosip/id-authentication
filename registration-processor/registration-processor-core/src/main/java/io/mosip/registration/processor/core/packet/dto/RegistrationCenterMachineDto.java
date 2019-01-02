package io.mosip.registration.processor.core.packet.dto;
	
import java.time.LocalDateTime;

import lombok.Data;

/**
 * Instantiates a new registration center machine dto.
 */
@Data
public class RegistrationCenterMachineDto {
	
	/** The reg id. */
	private String regId;	
	
	/** The machine id. */
	private String machineId;
	
	/** The regcntr id. */
	private String regcntrId;
	
	/** The is active. */
	private Boolean isActive;
	
	/** The latitude. */
	private String latitude;
	
	/** The longitude. */
	private String longitude;
	
	/** The packet creation date. */
	private LocalDateTime packetCreationDate;
}
