package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.SyncControl;

/**
 *This DAO class will be used to get all the Sync job details.
 *
 * @author Chukka Sreekar
 * @since 1.0.0
 */
public interface SyncJobControlDAO {

	/**
	 * Gets the values for sync status.
	 *
	 * @return the syncInfo
	 */
	public SyncJobInfo getSyncStatus();

	
	
	/**
	 * The Class SyncJobInfo.
	 */
	public class SyncJobInfo {

		/** The comparable list. */
		private List<SyncControl> syncControlList;

		/** The sync count. */
		private double yetToExportCount;
		
		/** The last export registration list. */
		private List<Registration> lastExportRegistrationList;

		/**
		 * Instantiates a new sync job info.
		 *
		 * @param syncControlList 
		 * 				the sync control list
		 * @param yetToExportCount 
		 * 				the yet to export count
		 * @param lastExportRegistrationList 
		 * 				the last export registration list
		 */
		public SyncJobInfo(List<SyncControl> syncControlList, double yetToExportCount, List<Registration> lastExportRegistrationList) {
			super();
			this.syncControlList = syncControlList;
			this.yetToExportCount = yetToExportCount;
			this.lastExportRegistrationList = lastExportRegistrationList;
		}

		/**
		 * Gets the sync control list.
		 *
		 * @return the syncControlList
		 */
		public List<SyncControl> getSyncControlList() {
			return syncControlList;
		}

		/**
		 * Gets the yet to export count.
		 *
		 * @return the yetToExportCount
		 */
		public double getYetToExportCount() {
			return yetToExportCount;
		}

		/**
		 * Gets the last export registration list.
		 *
		 * @return the lastExportRegistrationList
		 */
		public List<Registration> getLastExportRegistrationList() {
			return lastExportRegistrationList;
		}
	}

	/**
	 * Update Sync control transaction.
	 *
	 * @param syncControl 
	 * 				sync control
	 * @return sync control
	 */
	public SyncControl update(SyncControl syncControl);

	/**
	 * Save Sync Control Transaction.
	 *
	 * @param syncControl            
	 * 				sync Control
	 * @return sync control
	 */
	public SyncControl save(SyncControl syncControl);

	/**
	 * Get Sync Control data using jobId.
	 *
	 * @param syncJobId            
	 * 				id
	 * @return SyncControl data
	 */
	public SyncControl findBySyncJobId(String syncJobId);
	
	/**
	 * Get all sync control records.
	 *
	 * @return list of syncControl
	 */
	public List<SyncControl> findAll();
	
	/**
	 * Get Registration Details.
	 *
	 * @return list of Registration
	 */
	List<Registration> getRegistrationDetails();
}
