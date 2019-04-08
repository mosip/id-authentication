package io.mosip.kernel.idgenerator.vid.repository;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.vid.entity.VidSequence;

/**
 * Repository for sequence number for vid generation algorithm.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public interface VidSequenceRepository extends BaseRepository<VidSequence, String> {
	/**
	 * This method returns maximum sequence counter from database.
	 * 
	 * @return the {@link VidSequence}.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("from VidSequence v WHERE v.sequenceNumber=(select max(vs.sequenceNumber) from VidSequence vs)")
	VidSequence findMaxSequence();
}
