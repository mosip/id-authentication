package io.mosip.kernel.idgenerator.tokenid.repository;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.tokenid.entity.TokenIdSequence;

/**
 * Repository for sequence counter number of tokenid generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Repository
public interface TokenIdSequenceRepository extends BaseRepository<TokenIdSequence, String> {

	/**
	 * This method returns maximum sequence counter from database.
	 * 
	 * @return the {@link TokenIdSequence}.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("from TokenIdSequence t WHERE t.sequenceNumber=(select max(t1.sequenceNumber) from TokenIdSequence t1)")
	TokenIdSequence findMaxSequence();

}
