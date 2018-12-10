package io.mosip.registration.processor.packet.storage.repository;

import java.util.Date;
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
			+ " WHERE ide.id.regId=:regId")
	public List<Object[]> getApplicantInfo(@Param("regId") String regId);

	@Query("SELECT osi FROM RegOsiEntity osi WHERE osi.id.regId=:regId")
	public List<E> findByRegOsiId(@Param("regId") String regId);

	@Query("SELECT demo FROM IndividualDemographicDedupeEntity demo WHERE demo.id.regId=:regId")
	public List<E> findDemoById(@Param("regId") String regId);

	@Query("SELECT  demo FROM IndividualDemographicDedupeEntity demo WHERE demo.uinRefId is NOT NULL and demo.phoneticName:=pheoniticName and demo.gender:=gender and demo.dob:=dob")
	public List<E> getAllDemoWithUIN(@Param("pheoniticName") String pheoniticName, @Param("gender") String gender,
			@Param("dob") Date dob);

	@Query("SELECT applicant.imageName FROM ApplicantIrisEntity applicant WHERE applicant.id.regId=:regId")
	public List<String> getApplicantIrisImageNameById(@Param("regId") String regId);

	@Query("SELECT applicant.imageName FROM ApplicantFingerprintEntity applicant WHERE applicant.id.regId=:regId")
	public List<String> getApplicantFingerPrintImageNameById(@Param("regId") String regId);

}
