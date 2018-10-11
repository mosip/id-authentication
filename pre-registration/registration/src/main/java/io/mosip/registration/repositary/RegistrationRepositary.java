package io.mosip.registration.repositary;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.entity.RegistrationEntity;

@Repository("registrationRepository")
public interface RegistrationRepositary extends BaseRepository<RegistrationEntity, String> {

	public List<RegistrationEntity>  findBygroupId(String groupId);
	
	public static final String record = "SELECT prereg_id FROM prereg.applicant_demographic WHERE group_id= :groupId";

	@Query(value = record, nativeQuery = true)
	List<String> findBygroupIds(@Param("groupId") String groupId);
}
