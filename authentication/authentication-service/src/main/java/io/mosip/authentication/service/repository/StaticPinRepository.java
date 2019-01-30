package io.mosip.authentication.service.repository;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.entity.StaticPinEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
/**
 *  This is a repository class for entity {@link StaticPinEntity}.
 *  
 * @author Prem Kumar
 *
 */
@Repository
public interface StaticPinRepository extends BaseRepository<StaticPinEntity, String> {
	

}
