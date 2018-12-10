package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Dto for response to user for user machine mappings
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 */
@Data


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
