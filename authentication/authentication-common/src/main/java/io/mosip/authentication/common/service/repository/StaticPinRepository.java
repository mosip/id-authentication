package io.mosip.authentication.common.service.repository;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.StaticPin;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * The Interface StaticPinRepository.
 * 
 * @author Prem Kumar
 */
@Repository
public interface StaticPinRepository extends BaseRepository<StaticPin, String> {
	

}
