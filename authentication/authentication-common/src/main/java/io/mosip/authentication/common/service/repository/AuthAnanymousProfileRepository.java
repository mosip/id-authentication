package io.mosip.authentication.common.service.repository;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.AnanymousProfileEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * This is a repository class for entity {@link AnanymousProfileEntity}.
 * 
 * @author Loganathan Sekar
 */
@Repository
public interface AuthAnanymousProfileRepository extends BaseRepository<AnanymousProfileEntity, String> {
}
