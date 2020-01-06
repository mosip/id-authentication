package io.mosip.registration.dto.mastersync;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * The Class for SyncJobDef Dto.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SyncJobDefDto extends MasterSyncBaseDto {

	/** The id. */
	private String id;

	/** The name. */

	private String name;

	/** The api name. */

	private String apiName;

	/** The parent sync job id. */

	private String parentSyncJobId;

	/** The sync freq. */

	private String syncFreq;

	/** The lock duration. */

	private String lockDuration;

	/** The is active. */
	private Boolean isActive;

	/** The lang code. */
	private String langCode;

}
