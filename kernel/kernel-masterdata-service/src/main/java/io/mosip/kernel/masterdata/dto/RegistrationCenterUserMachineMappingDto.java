package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.RequiredArgsConstructor;
/**
 * Dto for response to user for user machine mappings
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 */
@Data
@RequiredArgsConstructor
public class RegistrationCenterUserMachineMappingDto {

	/**
	 * Center Id for request
	 */
	@NotNull
	@Size(min = 1, max = 10)
	private String cntrId;

	/**
	 * Machine Id for request
	 */
	@NotNull
	@Size(min = 1, max = 10)
	private String machineId;
	
	/**
	 * User Id for request
	 */
	@NotNull
	@Size(min = 1, max = 36)
	private String usrId;
	
	/**
	 * Is active for request
	 */
	@NotNull
	private Boolean isActive;
}
