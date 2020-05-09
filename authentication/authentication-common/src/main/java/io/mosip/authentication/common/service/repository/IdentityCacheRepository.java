package io.mosip.authentication.common.service.repository;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * Repository class for Identity Cache table
 * 
 * @author Loganathan Sekar
 *
 */
@Repository
public interface IdentityCacheRepository extends BaseRepository<IdentityEntity, String> {

}
