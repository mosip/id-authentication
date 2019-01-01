package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.SyncControl;

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
	/**
	 * Instantiates a new sync job info.
	 *
	 * @param comparableList
	 *            the comparable list
	 * @param syncCount
	 *            the sync count
	 */
	public class SyncJobInfo {

		/** The comparable list. */
		private List<SyncControl> syncControlList;

		/** The sync count. */
		private double yetToExportCount;

		public SyncJobInfo(List<SyncControl> syncControlList, double yetToExportCount) {
			super();
			this.syncControlList = syncControlList;
			this.yetToExportCount = yetToExportCount;
		}

		/**
		 * @return the syncControlList
		 */
		public List<SyncControl> getSyncControlList() {
			return syncControlList;
		}

		/**
		 * @return the yetToExportCount
		 */
		public double getYetToExportCount() {
			return yetToExportCount;
		}
	}

	/**
	 * Update Sync control transaction
	 * @param syncControl sync control
	 * @return sync control
	 */
	public SyncControl update(SyncControl syncControl);

	/**
	 * Save Sync Control Transaction
	 * 
	 * @param syncControl
	 *            sync Control
	 * @return sync control
	 */
	public SyncControl save(SyncControl syncControl);

	/**
	 * Get Sync Control data using jobId
	 * 
	 * @param syncJobId
	 *            id
	 * @return SyncControl data
	 */
	public SyncControl findBySyncJobId(String syncJobId);
}
