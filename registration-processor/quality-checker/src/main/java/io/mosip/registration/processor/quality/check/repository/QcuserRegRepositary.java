package io.mosip.registration.processor.quality.check.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.processor.quality.check.entity.BaseQcuserEntity;

@Repository
public interface QcuserRegRepositary<T extends BaseQcuserEntity<?>, E> extends BaseRepository<T, E> {

	@Query("SELECT qcUser FROM QcuserRegistrationIdEntity qcUser WHERE qcUser.id.usrId=:qcuserId")
	public List<T> findByUserId(@Param("qcuserId") String qcuserId);

	@Query("SELECT qcUser.id FROM UserDetailEntity qcUser WHERE qcUser.isActive=TRUE")
	public List<E> findAllUserIds();

	@Query("SELECT ape,ade FROM ApplicantPhotographEntity ape, IndividualDemographicDedupeEntity ade"
			+ " WHERE ade.id.regId=:refId")
	public List<Object[]> getApplicantInfo(@Param("refId") String regId);

}