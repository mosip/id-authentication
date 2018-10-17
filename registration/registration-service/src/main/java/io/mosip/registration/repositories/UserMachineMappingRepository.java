package io.mosip.registration.repositories;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.UserMachineMappingID;

/**
 * The reposistory interface for {@link UserMachineMapping} entity
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Repository
public interface UserMachineMappingRepository extends BaseRepository<UserMachineMapping, UserMachineMappingID>{

}
