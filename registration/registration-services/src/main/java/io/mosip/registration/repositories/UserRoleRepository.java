package io.mosip.registration.repositories;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.UserRole;
import io.mosip.registration.entity.id.UserRoleId;

/**
 * Interface for {@link UserRole}
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface UserRoleRepository extends BaseRepository<UserRole, UserRoleId> {

void deleteByUserRoleIdUsrId(String usrId);

}
