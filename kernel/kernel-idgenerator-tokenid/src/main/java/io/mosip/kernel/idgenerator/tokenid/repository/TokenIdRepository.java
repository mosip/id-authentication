package io.mosip.kernel.idgenerator.tokenid.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.kernel.idgenerator.tokenid.entity.TokenId;
/**
 * Spring JPA repository class which handles database calls. It is 
 * derived from {@link BaseRepository}}.
 * @author M1046464
 *
 */
@Repository
public interface TokenIdRepository extends BaseRepository<TokenId, Long> {

}
