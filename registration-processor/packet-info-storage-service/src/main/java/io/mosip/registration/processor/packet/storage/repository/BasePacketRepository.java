package io.mosip.registration.processor.packet.storage.repository;

import java.util.List;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.processor.packet.storage.entity.BasePacketEntity;

/**
 * The Interface BasePacketRepository.
 *
 * @author Girish Yarru
 * @param <E>
 *            the element type
 * @param <T>
 *            the generic type
 */
@Repository
public interface BasePacketRepository<E extends BasePacketEntity<?>, T> extends BaseRepository<E, T> {

	@Query("SELECT qcUser FROM QcuserRegistrationIdEntity qcUser WHERE qcUser.id.usrId=:qcuserId")
	public List<E> findByUserId(@Param("qcuserId") String qcuserId);

	@Query("SELECT ape,ide FROM ApplicantPhotographEntity ape, IndividualDemographicDedupeEntity ide"
			+ " WHERE ide.id.refId=:refId")
	public List<Object[]> getApplicantInfo(@Param("refId") String regId);

	@Query("SELECT osi FROM RegOsiEntity osi WHERE osi.id.regId=:regId")
	public List<E> findByRegOsiId(@Param("regId") String regId);
	
	/**
	 * This method gets the first created registration record
	 * {@link ManualVerificationEntity} with the specified status
	 * 
	 * @param statusCode
	 *            The statusCode
	 * @return {@link ManualVerificationEntity}
	 */
	@Query(value = "SELECT mve FROM ManualVerificationEntity mve WHERE mve.crDtimes in "
			+ "(SELECT min(mve2.crDtimes) FROM ManualVerificationEntity mve2 where mve2.statusCode=:statusCode) and mve.statusCode=:statusCode limit 1", nativeQuery=true)
	public List<E> getFirstApplicantDetails(@Param("statusCode") String statusCode);

	/**
	 * This method returns {@link ManualVerificationEntity} corresponding to
	 * specified registration Id and manual verifier user Id
	 * 
	 * @param regId
	 *            The registration Id
	 * @param mvUserId
	 *            The manual verifier user Id
	 * @return {@link ManualVerificationEntity}
	 */
	@Query("SELECT mve FROM ManualVerificationEntity mve where mve.id.regId=:regId and mve.mvUsrId=:mvUserId and mve.id.matchedRefId=:refId and mve.statusCode=:statusCode")
	public List<E> getSingleAssignedRecord(@Param("regId") String regId,@Param("refId") String refId,@Param("mvUserId") String mvUserId, @Param("statusCode") String statusCode);
	
	@Query("SELECT mve FROM ManualVerificationEntity mve where mve.mvUsrId=:mvUserId and mve.statusCode=:statusCode")
	public List<E> getAssignedApplicantDetails(@Param("mvUserId") String mvUserId, @Param("statusCode") String statusCode);
}
