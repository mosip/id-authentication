package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.SyncTransaction;

/**
 * To save the sync transaction details
 * @author Dinesh Ashokan
 *
 */
public interface SyncTransactionRepository extends BaseRepository<SyncTransaction, String>{

	
}
