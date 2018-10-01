package io.mosip.registration.processor.status.repositary;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;

@Repository
public interface RegistrationStatusRepositary extends BaseRepository<RegistrationStatusEntity, String> {

}
