package io.mosip.registration.repositories;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.MachineType;
import io.mosip.registration.entity.id.CodeAndLanguageCodeID;

/**
 * Repository to perform CRUD operations on MachineType.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface MachineTypeRepository extends BaseRepository<MachineType, CodeAndLanguageCodeID> {

}
