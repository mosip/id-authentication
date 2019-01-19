package io.mosip.registration.processor.quality.check.repository;
	
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.processor.quality.check.entity.BaseQcuserEntity;

/**
 * The Interface QcuserRegRepositary.
 *
 * @param <T> the generic type
 * @param <E> the element type
 */
@Repository
public interface QcuserRegRepositary<T extends BaseQcuserEntity<?>, E> extends BaseRepository<T, E> {

	/**
	 * Find by user id.
	 *
	 * @param qcuserId the qcuser id
	 * @return the list
	 */
	@Query("SELECT qcUser FROM QcuserRegistrationIdEntity qcUser WHERE qcUser.id.usrId=:qcuserId")
	public List<T> findByUserId(@Param("qcuserId") String qcuserId);

	/**
	 * Find all user ids.
	 *
	 * @return the list
	 */
	@Query("SELECT qcUser.id FROM UserDetailEntity qcUser WHERE qcUser.isActive=TRUE")
	public List<E> findAllUserIds();

	/**
	 * Gets the applicant info.
	 *
	 * @param regId the reg id
	 * @return the applicant info
	 */
	@Query("SELECT ape,ade FROM ApplicantPhotographEntity ape, IndividualDemographicDedupeEntity ade"
			+ " WHERE ade.id.regId=:refId")
	public List<Object[]> getApplicantInfo(@Param("refId") String regId);

}