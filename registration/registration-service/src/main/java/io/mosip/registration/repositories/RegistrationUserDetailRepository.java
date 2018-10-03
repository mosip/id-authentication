package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import io.mosip.registration.entity.RegistrationUserDetail;

@Repository
public interface RegistrationUserDetailRepository extends BaseRepository<RegistrationUserDetail, String>{
	
	List<RegistrationUserDetail> findByIdAndIsActiveTrue(String id);
}
