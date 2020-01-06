package io.mosip.preregistration.booking.serviceimpl.repository;

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
	
	/**
	 * @param preRegId
	 *            pass preRegId
	 * @return preregistration date for a pre-id
	 */
	public DemographicEntity findBypreRegistrationId(@Param("preRegId") String preRegId);

}
