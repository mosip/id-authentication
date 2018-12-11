package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
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
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime effectDateTime;
	

}
