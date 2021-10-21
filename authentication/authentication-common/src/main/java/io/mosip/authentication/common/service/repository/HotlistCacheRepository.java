package io.mosip.authentication.common.service.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.HotlistCache;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * The Interface HotlistCacheRepository.
 * 
 * @author Manoj SP
 */
@Repository
public interface HotlistCacheRepository extends BaseRepository<HotlistCache, String> {
	
	Optional<HotlistCache> findByIdHashAndIdType(String idHash, String idType);
}
