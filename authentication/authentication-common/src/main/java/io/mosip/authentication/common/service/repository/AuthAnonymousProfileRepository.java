package io.mosip.authentication.common.service.repository;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.AnonymousProfileEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * This is a repository class for entity {@link AnonymousProfileEntity}.
 * 
 * @author Loganathan Sekar
 */
@Repository
public interface AuthAnonymousProfileRepository extends BaseRepository<AnonymousProfileEntity, String> {
}
