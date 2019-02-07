package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUser;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterUserID;

/**
 * Repository for Center User Mapping
 * 
 * @author Neha
 * @since 1.0.0
 */
@Repository
public interface RegistrationCenterUserRepository extends BaseRepository<RegistrationCenterUser, RegistrationCenterUserID> {

	

	@Query("FROM RegistrationCenterUser WHERE registrationCenterUserID.regCenterId=?1 and (isDeleted=false or isDeleted is null) ")
	List<RegistrationCenterUser> findByRegistrationCenterUserByRegCenterId(String regCenter);
	
	@Query("FROM RegistrationCenterUser rcu WHERE rcu.registrationCenterUserID.regCenterId = ?1 AND (rcu.createdDateTime > ?2 OR rcu.updatedDateTime > ?2 OR rcu.deletedDateTime > ?2)")
	public List<RegistrationCenterUser> findAllByRegistrationCenterIdCreatedUpdatedDeleted(String regId,
			LocalDateTime lastUpdated) ;

	@Query("FROM RegistrationCenterUser rcu WHERE rcu.registrationCenterUserID.regCenterId = ?1")
	public List<RegistrationCenterUser> findAllByRegistrationCenterId(String regId);

}
