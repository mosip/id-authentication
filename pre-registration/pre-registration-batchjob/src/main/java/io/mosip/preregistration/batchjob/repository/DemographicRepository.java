package io.mosip.preregistration.batchjob.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.core.common.entity.DemographicEntity;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Repository("demographicRepository")
public interface DemographicRepository extends BaseRepository<DemographicEntity, String> {

	DemographicEntity findBypreRegistrationId(@Param("preRegId")String preRegId);
}
