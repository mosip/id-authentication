package io.mosip.registration.dto.mastersync;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * Class AppRolePriorityDto.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */


@Data
@EqualsAndHashCode(callSuper=false)
/**
 * Instantiates a new app role priority dto.
 */
@NoArgsConstructor

/**
 * Instantiates a new app role priority dto.
 *
 * @param appId the app id
 * @param processId the process id
 * @param roleCode the role code
 * @param priority the priority
 * @param langCode the lang code
 */
@AllArgsConstructor
public class AppRolePriorityDto extends MasterSyncBaseDto {

	/** The app id. */

	private String appId;

	/** The process id. */

	private String processId;

	/** The role code. */

	private String roleCode;

	/** The priority. */
	private Integer priority;

	/** The lang code. */
	private String langCode;
	
	/** The is Active. */
	private Boolean isActive;
}
