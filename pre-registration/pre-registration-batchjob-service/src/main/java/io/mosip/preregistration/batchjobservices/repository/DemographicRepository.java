package io.mosip.preregistration.batchjobservices.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.core.common.entity.DemographicEntity;

/**
 * @author Kishan Rathore
 *
 */
@Repository("demographicRepository")
public interface DemographicRepository extends BaseRepository<DemographicEntity, String> {

	DemographicEntity findBypreRegistrationId(@Param("preRegId")String preRegId);
}
