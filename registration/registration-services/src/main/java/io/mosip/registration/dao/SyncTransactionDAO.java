package io.mosip.registration.dao;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.registration.entity.SyncTransaction;

/**
 * To save and update the Sync Transactions in the DB
 * 
 * @author Dinesh Ashokan
 * 
 * @since 1.0.0
 *
 */
public interface SyncTransactionDAO {
	/**
	 * To save {@link SyncTransaction}
	 * 
	 * @param syncTransaction
	 *            Details
	 * @return sync transaction
	 */
	SyncTransaction save(SyncTransaction syncTransaction);

	/**
	 * Get All sync Transaction
	 * 
	 * @return list of sync transaction
	 */
	List<SyncTransaction> getAll();

	/**
	 * Get All sync Transaction
	 * 
	 * @param req
	 *            time
	 * @param syncJobId job ID
	 * @return list of sync transaction
	 */
	List<SyncTransaction> getSyncTransactions(Timestamp req, String syncJobId);

	/**
	 * Get All Sync Transactions
	 * 
	 * @param syncJobId
	 *            id
	 * @param previousFiredTime
	 *            previous trigger time
	 * @param currentFiredTime
	 *            current trigger time
	 * @return list of sync transactions
	 */
	List<SyncTransaction> getAll(String syncJobId, Timestamp previousFiredTime, Timestamp currentFiredTime);
}
