package io.mosip.admin.accountmgmt.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.admin.accountmgmt.entity.RegistrationCenterUser;
import io.mosip.admin.accountmgmt.entity.id.RegistrationCenterUserID;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * Repository for Center User Mapping
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
@Repository
public interface RegistrationCenterUserRepository
		extends BaseRepository<RegistrationCenterUser, RegistrationCenterUserID> {

	/**
	 * 
	 * @param regId
	 *            - registration center id
	 * @return list of {@link RegistrationCenterUser} - list of registration center
	 *         user
	 */
	@Query("FROM RegistrationCenterUser rcu WHERE rcu.registrationCenterUserID.regCenterId = ?1")
	public List<RegistrationCenterUser> findAllByRegistrationCenterId(String regId);

}
