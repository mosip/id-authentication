package org.mosip.registration.processor.status.repositary;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationStatusRepositary extends BaseRepository<RegistrationStatusEntity, String> {

}
