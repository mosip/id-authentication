package io.mosip.registration.repositories;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.SyncTransaction;

/**
 * To save the sync transaction details
 * @author Dinesh Ashokan
 *
 */
public interface SyncTransactionRepository extends BaseRepository<SyncTransaction, String>{

	/**
	 * Get All sync Transaction
	 * @param request time
	 * @return list of sync transaction
	 */
	List<SyncTransaction> findByCrDtimeAfter(Timestamp req);

	
}
