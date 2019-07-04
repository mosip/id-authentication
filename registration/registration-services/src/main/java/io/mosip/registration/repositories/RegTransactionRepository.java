package io.mosip.registration.repositories;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegistrationTransaction;

/**
 * The reposistory interface for {@link RegistrationTransaction} entity
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface RegTransactionRepository extends BaseRepository<RegistrationTransaction, String> {

	public void deleteByRegId(String regId);
}
