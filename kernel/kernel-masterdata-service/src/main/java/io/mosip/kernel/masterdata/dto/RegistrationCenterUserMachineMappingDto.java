package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * Dto for response to user for user machine mappings
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 */
@Data
@ApiModel(description = "Model representing a Registration-Center-User-Machine-Mapping Request")
public class RegistrationCenterUserMachineMappingDto {

	/**
	 * Center Id for request
	 */
	@NotNull
	@Size(min = 1, max = 10)
	@ApiModelProperty(notes = "Registration Center Id for request", example = "RC001", required = true)
	private String cntrId;

	/**
	 * Machine Id for request
	 */
	@NotNull
	@Size(min = 1, max = 10)
	@ApiModelProperty(notes = "Machine Id for request", example = "MC001", required = true)
	private String machineId;
	
	/**
	 * User Id for request
	 */
	@NotNull
	@Size(min = 1, max = 36)
	@ApiModelProperty(notes = "User Id for request", example = "QC001", required = true)
	private String usrId;
	
	/**
	 * Is active for request
	 */
	@NotNull
	@ApiModelProperty(notes = "mapping is active or not", required = true)
	private Boolean isActive;
}
