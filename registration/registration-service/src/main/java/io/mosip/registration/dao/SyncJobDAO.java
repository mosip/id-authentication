package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.SyncControl;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DAO class for SyncJobDAO.
 *
 * @author Chukka Sreekar
 * @since 1.0.0
 */
public interface SyncJobDAO {

	
	
	/**
	 * Gets the values for sync status.
	 *
	 * @return the syncInfo
	 */
	public SyncJobInfo getSyncStatus();
	
	
	
	/**
	 * Gets the sync count.
	 *
	 * @return the sync count
	 */
	@Getter
	/**
	 * Instantiates a new sync job info.
	 *
	 * @param comparableList the comparable list
	 * @param syncCount the sync count
	 */
	@AllArgsConstructor
	public class SyncJobInfo {
		
		/** The comparable list. */
		private List<SyncControl> syncControlList ;
		
		/** The sync count. */
		private double yetToExportCount;
	}

	
}
