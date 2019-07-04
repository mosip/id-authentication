package io.mosip.registration.repositories;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegCenterUser;
import io.mosip.registration.entity.id.RegCenterUserId;

/**
 * Repository for Center User Mapping
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Repository
public interface RegistrationCenterUserRepository extends BaseRepository<RegCenterUser, RegCenterUserId> {

}
