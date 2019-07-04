package io.mosip.registration.repositories;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.AppDetail;
import io.mosip.registration.entity.id.IdAndLanguageCodeID;


/**
 * Interface AppDetailRepository.
 * @author Sreekar Chukka
 * @since 1.0.0
 * 
 */
@Repository
public interface AppDetailRepository extends BaseRepository<AppDetail, IdAndLanguageCodeID> {


}
