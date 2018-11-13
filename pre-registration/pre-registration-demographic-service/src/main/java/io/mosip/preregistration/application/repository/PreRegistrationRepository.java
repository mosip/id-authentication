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

	@Query(value = record, nativeQuery = true)
	List<String> findBygroupIds(@Param("groupId") String groupId);

	public List<PreRegistrationEntity> findBygroupId(String groupId);

	@Query("SELECT e FROM PreRegistrationEntity e  WHERE e.cr_appuser_id=:userId")
	public List<PreRegistrationEntity> findByuserId(@Param("userId")String userId);

	@Query(value = countRec, nativeQuery = true)
	public List<String> noOfGroupIds(@Param("userId") String userId);

	@Query("SELECT r FROM PreRegistrationEntity r  WHERE r.preRegistrationId=:preRegId")
	public PreRegistrationEntity findBypreRegistrationId(@Param("preRegId")String preRegId);

//	public List<PreRegistrationEntity> findByGroupIdAndIsPrimary(String groupId, boolean isPrimary);

	public void deleteAllBygroupId(String groupId);

	public void deleteByPreRegistrationId(String preId);
	

//	public void deleteByGroupIdAndIsPrimary(String groupId, boolean isPrimary);
}
