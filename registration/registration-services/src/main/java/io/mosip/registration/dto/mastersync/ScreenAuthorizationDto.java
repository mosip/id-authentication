package io.mosip.registration.dto.mastersync;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Screen authorizaton DTO class
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ScreenAuthorizationDto extends MasterSyncBaseDto {

	private String screenId;

	private String roleCode;

	private Boolean isPermitted;
	
	private String langCode;

	/** The is Active. */
	private Boolean isActive;
}
