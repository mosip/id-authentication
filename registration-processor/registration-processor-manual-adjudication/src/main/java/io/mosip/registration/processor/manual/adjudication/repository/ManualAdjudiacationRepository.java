package io.mosip.registration.processor.manual.adjudication.repository;

import org.springframework.stereotype.Repository;


import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationEntity;

@Repository
public interface ManualAdjudiacationRepository<T extends ManualVerificationEntity, E> extends BaseRepository<T, E> {

}
