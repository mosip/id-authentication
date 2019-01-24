package io.mosip.kernel.syncdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUser;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterUserID;

/**
 * This Class takes care in communicating with the Database. It extends class {@link BaseRepository}}
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterUserRepository extends BaseRepository<RegistrationCenterUser, RegistrationCenterUserID> {

	@Query("FROM RegistrationCenterUser WHERE registrationCenterUserID.regCenterId=?1 and (isDeleted=false or isDeleted is null) ")
	List<RegistrationCenterUser> findByRegistrationCenterUserByRegCenterId(String regCenter);
	
}
