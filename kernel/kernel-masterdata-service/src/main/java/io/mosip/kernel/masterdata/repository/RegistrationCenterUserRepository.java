package io.mosip.kernel.masterdata.repository;

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
}
