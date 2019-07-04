package io.mosip.kernel.idgenerator.tokenid.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.tokenid.entity.TokenIdSeed;

/**
 * Repository for seed value of tokenid generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Repository
public interface TokenIdSeedRepository extends BaseRepository<TokenIdSeed, String> {

}
