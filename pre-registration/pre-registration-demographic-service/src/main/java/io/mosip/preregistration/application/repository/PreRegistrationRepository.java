package io.mosip.preregistration.application.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.application.entity.PreRegistrationEntity;

/**
 * Registration Repository
 * 
 * @author M1037462
 *
 */
@Repository("registrationRepository")
@Transactional
public interface PreRegistrationRepository extends BaseRepository<PreRegistrationEntity, String> {

	public static final String record = "SELECT prereg_id FROM prereg.applicant_demographic WHERE group_id= :groupId";
	public static final String countRec = "SELECT DISTINCT group_id  FROM prereg.applicant_demographic where cr_appuser_id=:userId";

	@Query("SELECT e FROM PreRegistrationEntity e  WHERE e.createdBy=:userId")
	public List<PreRegistrationEntity> findByuserId(@Param("userId") String userId);

	@Query(value = countRec, nativeQuery = true)
	public List<String> noOfGroupIds(@Param("userId") String userId);

	@Query("SELECT r FROM PreRegistrationEntity r  WHERE r.preRegistrationId=:preRegId")
	public PreRegistrationEntity findBypreRegistrationId(@Param("preRegId") String preRegId);

	public boolean deleteByPreRegistrationId(String preId);

}
