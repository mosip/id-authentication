package org.mosip.registration.repositories;

import java.util.List;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.RegistrationUserDetail;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationUserDetailRepository extends BaseRepository<RegistrationUserDetail, String>{
	
	List<RegistrationUserDetail> findByIdAndIsActiveTrue(String id);
}
