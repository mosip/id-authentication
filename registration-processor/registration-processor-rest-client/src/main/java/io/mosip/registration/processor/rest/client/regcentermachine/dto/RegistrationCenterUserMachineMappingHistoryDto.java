package io.mosip.registration.processor.rest.client.regcentermachine.dto;


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
