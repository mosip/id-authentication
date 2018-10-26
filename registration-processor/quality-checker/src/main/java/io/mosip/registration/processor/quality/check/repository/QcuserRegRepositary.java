package io.mosip.registration.processor.quality.check.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.processor.quality.check.entity.BaseQcuserEntity;

@Repository
public interface QcuserRegRepositary<T extends BaseQcuserEntity<?>, E> extends BaseRepository<T, E> {

	@Query("SELECT qcUser FROM QcuserRegistrationIdEntity qcUser WHERE qcUser.usrId=:qcuserId")
	public List<T> findByUserId(String qcuserId);
	
	@Query("SELECT ade,afe,aie,ape FROM  ApplicantDemographicEntity ade, ApplicantFingerprintEntity afe, ApplicantIrisEntity aie, ApplicantPhotographEntity ape"
            + "WHERE ade.regId=:regId AND afe.regId =:regId AND aie.regId =:regId AND ape.regId=:regId")
    public List<Object[]> getApplicantInfo(@Param("regId") String regId);
}