package io.mosip.preregistration.application.repository;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.application.entity.DemographicEntity;

/**
 * Registration Repository
 * 
 * @author M1037462
 *
 */
@Repository("registrationRepository")
@Transactional
public interface DemographicRepository extends BaseRepository<DemographicEntity, String> {

	public static final String record = "SELECT prereg_id FROM prereg.applicant_demographic WHERE group_id= :groupId";
	public static final String countRec = "SELECT DISTINCT group_id  FROM prereg.applicant_demographic where cr_appuser_id=:userId";

	@Query("SELECT e FROM DemographicEntity e  WHERE e.createdBy=:userId")
	public List<DemographicEntity> findByuserId(@Param("userId") String userId);

	@Query(value = countRec, nativeQuery = true)
	public List<String> noOfGroupIds(@Param("userId") String userId);

	@Query("SELECT r FROM DemographicEntity r  WHERE r.preRegistrationId=:preRegId")
	public DemographicEntity findBypreRegistrationId(@Param("preRegId") String preRegId);

	public int deleteByPreRegistrationId(String preId);
	
	public List<DemographicEntity> findBycreateDateTimeBetween(Timestamp start,Timestamp end);


}
