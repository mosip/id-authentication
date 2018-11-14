package io.mosip.registration.dao;

import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncTransaction;

/**
 * To save and update batch process
 * 
 * @author Dinesh Ashokan
 * 
 * @since1.0.0
 *
 */
public interface JobTransactionDAO {
	/**
	 * To save {@link SyncTransaction}
	 * 
	 * @param syncTransaction
	 *            Details
	 * @return
	 */
	String saveSyncTransaction(SyncTransaction syncTransaction);

	/**
	 * To upadte {@link SyncControl}
	 * 
	 * @param syncControl
	 *            Details
	 * @return
	 */
	String upadteSyncControl(SyncControl syncControl);
}
