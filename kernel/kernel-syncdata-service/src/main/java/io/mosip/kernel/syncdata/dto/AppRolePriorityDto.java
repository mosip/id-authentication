package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Class AppRolePriorityDto.
 *
 * @author Srinivasan
 * @since 1.0.0
 */

@Data
@EqualsAndHashCode(callSuper = false)
/**
 * Instantiates a new app role priority dto.
 */
@NoArgsConstructor

/**
 * Instantiates a new app role priority dto.
 *
 * @param appId     the app id
 * @param processId the process id
 * @param roleCode  the role code
 * @param priority  the priority
 * @param langCode  the lang code
 */
@AllArgsConstructor
public class AppRolePriorityDto extends BaseDto {

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
}
