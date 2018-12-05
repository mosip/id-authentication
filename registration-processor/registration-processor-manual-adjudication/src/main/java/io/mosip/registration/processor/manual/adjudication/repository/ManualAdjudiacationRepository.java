package io.mosip.registration.processor.manual.adjudication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationEntity;
import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationPKEntity;

@Repository
public interface ManualAdjudiacationRepository<T extends ManualVerificationEntity, E extends ManualVerificationPKEntity> extends BaseRepository<T, E> {

	@Query("SELECT mve FROM ManualVerificationEntity mve WHERE mve.crDtimes in "
			+ "(SELECT min(mve2.crDtimes) FROM ManualVerificationEntity mve2)"
			+ " and mve.statusCode=:statusCode")
	public List<ManualVerificationEntity> getFirstApplicantDetails(@Param("statusCode") String statusCode);
	
	@Query("SELECT mve FROM ManualVerificationEntity mve where mve.pkId.regId=:regId and mve.mvUsrId=:mvUserId")
	public ManualVerificationEntity getByRegId(@Param("regId") String regId,@Param("mvUserId") String mvUserId);
	
	
	
	
}
