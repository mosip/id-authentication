package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUser;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterUserID;

/**
 * Registration Center User Repository
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public interface RegistrationCenterUserRepository
		extends BaseRepository<RegistrationCenterUser, RegistrationCenterUserID> {

	@Query(value = "select count(*) from  master.reg_center_user where regcntr_id=?1 and (is_deleted is null or is_deleted=false);", nativeQuery = true)
	public Long countCenterUsers(String centerId);

	/**
	 * Method that returns the list of registration centers mapped to users.
	 * 
	 * @param regCenterID
	 *            the center ID of the reg-center which needs to be decommissioned.
	 * @return the list of registration centers mapped to users.
	 */
	@Query(value = "FROM RegistrationCenterUser ru WHERE ru.registrationCenterUserID.regCenterId =?1 and (ru.isDeleted is null or ru.isDeleted =false) and ru.isActive = true")
	public List<RegistrationCenterUser> registrationCenterUserMappings(String regCenterID);
	
	/**
	 * Method to find valid RegistrationCenterUser based on User Id and Registration Center Id 
	 * Type code provided.
	 * 
	 * @param userId
	 *            the User Id.
	 * @param regCenterId
	 *            the registration center Id .
	 * @return RegistrationCenterUser
	 */
	@Query(value = "FROM RegistrationCenterUser rc WHERE rc.registrationCenterUserID.userId = ?1 and rc.registrationCenterUserID.regCenterId = ?2 AND (rc.isDeleted is null OR rc.isDeleted = false)" )
	public RegistrationCenterUser findByUserIdAndRegCenterId(String userId, String regCenterId);
}
