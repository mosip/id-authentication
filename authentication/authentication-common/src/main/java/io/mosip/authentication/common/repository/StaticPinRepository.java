package io.mosip.authentication.common.repository;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.entity.StaticPin;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
/**
 *  This is a repository class for entity {@link StaticPin,BaseRepository}.
 *  
 * @author Prem Kumar
 *
 */
@Repository
public interface StaticPinRepository extends BaseRepository<StaticPin, String> {
	

}
