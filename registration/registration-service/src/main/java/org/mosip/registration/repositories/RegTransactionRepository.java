package org.mosip.registration.repositories;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.RegistrationTransaction;

/**
 * The reposistory interface for {@link RegistrationTransaction} entity
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface RegTransactionRepository extends BaseRepository<RegistrationTransaction, String> {

}
