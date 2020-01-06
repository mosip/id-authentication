package io.mosip.registration.repositories;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.ProcessList;
import io.mosip.registration.entity.id.IdAndLanguageCodeID;

/**
 * ProcessListRepository.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Repository
public interface ProcessListRepository extends BaseRepository<ProcessList, IdAndLanguageCodeID> {

}
