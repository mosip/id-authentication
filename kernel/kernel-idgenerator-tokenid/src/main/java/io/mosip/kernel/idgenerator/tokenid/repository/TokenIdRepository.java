package io.mosip.kernel.idgenerator.tokenid.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.kernel.idgenerator.tokenid.entity.TokenId;

@Repository
public interface TokenIdRepository extends BaseRepository<TokenId, Long> {

}
