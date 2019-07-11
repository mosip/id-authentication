package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUser;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterUserID;

/**
 * Repository for Center User Mapping
 * 
 * @author Neha
 * @since 1.0.0
 */
@Repository
public interface RegistrationCenterUserRepository
		extends JpaRepository<RegistrationCenterUser, RegistrationCenterUserID> {

	/**
	 * 
	 * @param regCenter - registration center id
	 * @return list of {@link RegistrationCenterUser} - list of registration center
	 *         user
	 */
	@Query("FROM RegistrationCenterUser WHERE registrationCenterUserID.regCenterId=?1 and (isDeleted=false or isDeleted is null) ")
	List<RegistrationCenterUser> findByRegistrationCenterUserByRegCenterId(String regCenter);

	/**
	 * 
	 * @param regId            - registration center id
	 * @param lastUpdated      - last updated time stamp
	 * @param currentTimeStamp - current time stamp
	 * @return list of {@link RegistrationCenterUser} - list of registration center
	 *         user
	 * 
	 */
	@Query("FROM RegistrationCenterUser rcu WHERE rcu.registrationCenterUserID.regCenterId = ?1 AND ((rcu.createdDateTime > ?2 AND rcu.createdDateTime<=?3) OR (rcu.updatedDateTime > ?2 AND rcu.updatedDateTime <=?3) OR (rcu.deletedDateTime > ?2 AND rcu.deletedDateTime <=?3))")
	public List<RegistrationCenterUser> findAllByRegistrationCenterIdCreatedUpdatedDeleted(String regId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp);

	/**
	 * 
	 * @param regId - registration center id
	 * @return list of {@link RegistrationCenterUser} - list of registration center
	 *         user
	 */
	@Query("FROM RegistrationCenterUser rcu WHERE rcu.registrationCenterUserID.regCenterId = ?1")
	public List<RegistrationCenterUser> findAllByRegistrationCenterId(String regId);

}
