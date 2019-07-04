package io.mosip.registration.repositories;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.UserPassword;

/**
 * Interface for {@link UserPassword}
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface UserPwdRepository extends BaseRepository<UserPassword, String> {
	
	void deleteByUsrId(String id);

}
