package io.mosip.preregistration.batchjob.repository;


import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.batchjob.entity.Applicant_demographic;

@Repository("preRegistrationDemographicRepository")
public interface PreRegistrationDemographicRepository extends BaseRepository<Applicant_demographic, String> {

	Applicant_demographic findBypreRegistrationId(@Param(value = "preRegId") String preRegId);
}
