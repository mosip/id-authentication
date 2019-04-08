package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// TODO: Auto-generated Javadoc
/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data

/* (non-Javadoc)
 * @see java.lang.Object#hashCode()
 */
@EqualsAndHashCode(callSuper = false)

/**
 * Instantiates a new sync job def dto.
 */
@NoArgsConstructor

/**
 * Instantiates a new sync job def dto.
 *
 * @param id the id
 * @param name the name
 * @param apiName the api name
 * @param parentSyncJobId the parent sync job id
 * @param syncFreq the sync freq
 * @param lockDuration the lock duration
 * @param isActive the is active
 * @param isDeleted the is deleted
 * @param langCode the lang code
 */
@AllArgsConstructor
public class SyncJobDefDto  {

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
	
	/** The is deleted. */
	private Boolean isDeleted;
	
	/** The lang code. */
	private String langCode;

}
