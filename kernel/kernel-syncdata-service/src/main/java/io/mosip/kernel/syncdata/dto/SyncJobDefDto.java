package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class SyncJobDefDto {

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

	/** The is deleted. */
	private Boolean isDeleted;

	/** The lang code. */
	private String langCode;

}
