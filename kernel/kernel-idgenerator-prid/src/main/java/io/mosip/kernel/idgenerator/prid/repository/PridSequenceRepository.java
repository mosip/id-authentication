package io.mosip.kernel.idgenerator.prid.repository;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.prid.entity.PridSequence;

/**
 * Repository for sequence number of prid generation algorithm.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Repository
public interface PridSequenceRepository extends BaseRepository<PridSequence, String> {
	/**
	 * This method returns maximum sequence counter from database.
	 * 
	 * @return the {@link PridSequence}.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("from PridSequence p WHERE p.sequenceNumber=(select max(ps.sequenceNumber) from PridSequence ps)")
	PridSequence findMaxSequence();
}
