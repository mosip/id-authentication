package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.SyncTransaction;

/**
 * To save and update batch process
 * 
 * @author Dinesh Ashokan
 * 
 * @since1.0.0
 *
 */
public interface SyncJobTransactionDAO {
	/**
	 * To save {@link SyncTransaction}
	 * 
	 * @param syncTransaction
	 *            Details
	 * @return
	 */
	SyncTransaction save(SyncTransaction syncTransaction);
	
	/**
	 * Get All sync Transaction
	 * @return list of sync transaction
	 */
	List<SyncTransaction> getAll();

}
