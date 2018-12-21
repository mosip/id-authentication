package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.entity.UserBiometricId;

public interface UserBiometricRepository extends BaseRepository<UserBiometric, UserBiometricId>{

	public List<UserBiometric> findByUserBiometricIdBioAttributeCodeAndIsActiveTrue(String attrCode);
	
	public List<UserBiometric> findByUserBiometricIdUsrIdAndIsActiveTrue(String userId);
}
