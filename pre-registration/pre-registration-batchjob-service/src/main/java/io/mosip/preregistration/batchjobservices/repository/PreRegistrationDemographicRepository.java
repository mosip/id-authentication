package io.mosip.preregistration.batchjobservices.repository;


import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.batchjobservices.entity.ApplicantDemographic;

/**
 * @author M1043008
 *
 */
@Repository("preRegistrationDemographicRepository")
public interface PreRegistrationDemographicRepository extends BaseRepository<ApplicantDemographic, String> {

	ApplicantDemographic findBypreRegistrationId(@Param(value = "preRegId") String preRegId);
}
