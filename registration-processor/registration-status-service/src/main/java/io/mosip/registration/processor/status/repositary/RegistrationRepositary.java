package io.mosip.registration.processor.status.repositary;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.processor.status.entity.BaseRegistrationEntity;

/**
 * The Interface RegistrationRepositary.
 *
 * @param <T> the generic type
 * @param <E> the element type
 */
@Repository
public interface RegistrationRepositary<T extends BaseRegistrationEntity, E> extends BaseRepository<T, E> {

}
