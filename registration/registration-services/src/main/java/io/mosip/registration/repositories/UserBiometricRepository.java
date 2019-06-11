package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.entity.id.UserBiometricId;

/**
 * Interface for {@link UserBiometric}
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface UserBiometricRepository extends BaseRepository<UserBiometric, UserBiometricId>{

	public List<UserBiometric> findByUserBiometricIdBioAttributeCodeAndIsActiveTrue(String attrCode);
	
	public List<UserBiometric> findByUserBiometricIdUsrIdAndIsActiveTrueAndUserBiometricIdBioTypeCodeIgnoreCase(String userId, String bioType);
	
	void deleteByUserBiometricIdUsrId(String userID);
}
