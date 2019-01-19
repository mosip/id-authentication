package io.mosip.registration.processor.core.packet.dto.regcentermachine;

	
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data

/**
 * Instantiates a new registration center user machine mapping history dto.
 *
 * @param cntrId the cntr id
 * @param machineId the machine id
 * @param usrId the usr id
 * @param isActive the is active
 */
@AllArgsConstructor

/**
 * Instantiates a new registration center user machine mapping history dto.
 */
@NoArgsConstructor
public class RegistrationCenterUserMachineMappingHistoryDto {

	/** Center Id for response. */
	private String cntrId;

	/** Machine Id for response. */
	private String machineId;
	
	/** User Id for response. */
	private String usrId;
	
	/** The is active. */
	private Boolean isActive;

}
