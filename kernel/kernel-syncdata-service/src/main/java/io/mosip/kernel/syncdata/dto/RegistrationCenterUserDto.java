package io.mosip.kernel.syncdata.dto;

import lombok.Data;

/**
 * DTO class for {@link RegistratonCenterUser}.
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Data

public class RegistrationCenterUserDto {

	private String regCenterId;
	
	private String userId;
	
	private Boolean isActive;
}
