package io.mosip.registration.processor.packet.storage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.processor.packet.storage.entity.BasePacketEntity;
import io.mosip.registration.processor.packet.storage.entity.ManualVerificationEntity;

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

	/**
	 * Find by user id.
	 *
	 * @param qcuserId the qcuser id
	 * @return the list
	 */
	@Query("SELECT qcUser FROM QcuserRegistrationIdEntity qcUser WHERE qcUser.id.usrId=:qcuserId")
	public List<E> findByUserId(@Param("qcuserId") String qcuserId);

	/**
	 * Gets the applicant info.
	 *
	 * @param regId the reg id
	 * @return the applicant info
	 */
	@Query("SELECT ape,ide FROM ApplicantPhotographEntity ape, IndividualDemographicDedupeEntity ide"
			+ " WHERE ide.id.regId=:regId")
	public List<Object[]> getApplicantInfo(@Param("regId") String regId);

	/**
	 * Find by reg osi id.
	 *
	 * @param regId the reg id
	 * @return the list
	 */
	@Query("SELECT osi FROM RegOsiEntity osi WHERE osi.id.regId=:regId")
	public List<E> findByRegOsiId(@Param("regId") String regId);

	/**
	 * Find demo by id.
	 *
	 * @param regId the reg id
	 * @return the list
	 */
	@Query("SELECT demo FROM IndividualDemographicDedupeEntity demo WHERE demo.id.regId=:regId")
	public List<E> findDemoById(@Param("regId") String regId);

	/**
	 * Gets the applicant iris image name by id.
	 *
	 * @param regId the reg id
	 * @return the applicant iris image name by id
	 */
	@Query("SELECT applicant.imageName FROM ApplicantIrisEntity applicant WHERE applicant.id.regId=:regId")
	public List<String> getApplicantIrisImageNameById(@Param("regId") String regId);

	/**
	 * Gets the applicant finger print image name by id.
	 *
	 * @param regId the reg id
	 * @return the applicant finger print image name by id
	 */
	@Query("SELECT applicant.imageName FROM ApplicantFingerprintEntity applicant WHERE applicant.id.regId=:regId")
	public List<String> getApplicantFingerPrintImageNameById(@Param("regId") String regId);

	/**
	 * This method gets the first created registration record
	 * {@link ManualVerificationEntity} with the specified status.
	 *
	 * @param statusCode            The statusCode
	 * @return {@link ManualVerificationEntity}
	 */
	@Query(value = "SELECT mve FROM ManualVerificationEntity mve WHERE mve.crDtimes in "
			+ "(SELECT min(mve2.crDtimes) FROM ManualVerificationEntity mve2 where mve2.statusCode=:statusCode) and mve.statusCode=:statusCode")
	public List<E> getFirstApplicantDetails(@Param("statusCode") String statusCode);

	/**
	 * This method returns {@link ManualVerificationEntity} corresponding to
	 * specified registration Id and manual verifier user Id.
	 *
	 * @param regId            The registration Id
	 * @param refId the ref id
	 * @param mvUserId            The manual verifier user Id
	 * @param statusCode the status code
	 * @return {@link ManualVerificationEntity}
	 */
	@Query("SELECT mve FROM ManualVerificationEntity mve where mve.id.regId=:regId and mve.mvUsrId=:mvUserId and mve.id.matchedRefId=:refId and mve.statusCode=:statusCode")
	public List<E> getSingleAssignedRecord(@Param("regId") String regId, @Param("refId") String refId,
			@Param("mvUserId") String mvUserId, @Param("statusCode") String statusCode);

	/**
	 * Gets the assigned applicant details.
	 *
	 * @param mvUserId the mv user id
	 * @param statusCode the status code
	 * @return the assigned applicant details
	 */
	@Query("SELECT mve FROM ManualVerificationEntity mve where mve.mvUsrId=:mvUserId and mve.statusCode=:statusCode")
	public List<E> getAssignedApplicantDetails(@Param("mvUserId") String mvUserId,
			@Param("statusCode") String statusCode);

	/**
	 * Update is active if duplicate found.
	 *
	 * @param regId the reg id
	 */
	@Modifying
	@Transactional
	@Query("UPDATE  IndividualDemographicDedupeEntity demo SET  demo.isActive = FALSE WHERE demo.id.regId =:regId")
	public void updateIsActiveIfDuplicateFound(@Param("regId") String regId);

	@Modifying
	@Transactional
	@Query("UPDATE  IndividualDemographicDedupeEntity demo SET  demo.uin =:uin WHERE demo.id.regId =:regId")
	public void updateUinWrtRegistraionId(@Param("regId") String regId,@Param("uin") String uin);


	@Query("SELECT demo.id.regId FROM IndividualDemographicDedupeEntity demo WHERE demo.uinRefId =:uin")
	public List<String> getRegIdByUIN(@Param("uin")String uin);
	
}
