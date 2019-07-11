
package io.mosip.registration.repositories;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.IdType;
import io.mosip.registration.entity.id.CodeAndLanguageCodeID;

/**
 * Interface for idtype repository.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface IdTypeRepository extends BaseRepository<IdType, CodeAndLanguageCodeID> {
	
}
