/**
 * 
 */
package io.mosip.registration.processor.status.repositary;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;

/**
 * The Class SyncRegistrationRepository.
 *
 * @author M1047487
 */
@Repository
public interface SyncRegistrationRepository extends BaseRepository<SyncRegistrationEntity, String>{


}
