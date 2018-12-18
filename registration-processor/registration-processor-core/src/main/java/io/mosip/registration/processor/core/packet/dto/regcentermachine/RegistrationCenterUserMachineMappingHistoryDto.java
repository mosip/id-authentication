package io.mosip.registration.processor.core.packet.dto.regcentermachine;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationCenterUserMachineMappingHistoryDto {

	/**
	 * Center Id for response
	 */
	private String cntrId;

	/**
	 * Machine Id for response
	 */
	private String machineId;
	
	/**
	 * User Id for response
	 */
	private String usrId;
	
	private Boolean isActive;

}
