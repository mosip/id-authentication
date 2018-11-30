package io.mosip.registration.repositories;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.PreregistrationTransaction;

/**
 * Pre registration transaction repository to get/save pre-reg transaction
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
public interface PreRegistrationTransactionRepository extends BaseRepository<PreregistrationTransaction, String> {
	
}
