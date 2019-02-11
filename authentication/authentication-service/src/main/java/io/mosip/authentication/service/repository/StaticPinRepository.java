package io.mosip.authentication.service.repository;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.entity.StaticPin;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
/**
 *  This is a repository class for entity {@link StaticPin}.
 *  
 * @author Prem Kumar
 *
 */
@Repository
public interface StaticPinRepository extends BaseRepository<StaticPin, String> {
	

}
