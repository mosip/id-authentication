package io.mosip.authentication.idgenerator.tokenid.repository;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.idgenerator.tokenid.entity.TokenIdSeed;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * Repository for seed value of tokenid generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Repository
public interface TokenIdSeedRepository extends BaseRepository<TokenIdSeed, String> {

}
