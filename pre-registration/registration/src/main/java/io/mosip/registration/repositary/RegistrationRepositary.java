package io.mosip.registration.repositary;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.entity.RegistrationEntity;

@Repository("registrationRepository")
public interface RegistrationRepositary extends BaseRepository<RegistrationEntity, String> {
	
	public static final String record = "SELECT prereg_id FROM prereg.applicant_demographic WHERE group_id= :groupId";
	public static final String countRec = "SELECT DISTINCT group_id  FROM prereg.applicant_demographic where userid=:userId";
	
	
	@Query(value = record, nativeQuery = true)
	List<String> findBygroupIds(@Param("groupId") String groupId);
	
	
	public List<RegistrationEntity>  findBygroupId(String groupId);
	
	public List<RegistrationEntity> findByuserId(String userId);

	@Query(value = countRec, nativeQuery = true)
	public List<String> noOfGroupIds(@Param("userId") String userId);

	
}
