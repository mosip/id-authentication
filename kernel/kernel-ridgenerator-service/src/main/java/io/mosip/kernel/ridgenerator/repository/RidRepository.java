package io.mosip.kernel.ridgenerator.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.ridgenerator.entity.Rid;
import io.mosip.kernel.ridgenerator.entity.id.CenterAndMachineId;

/**
 * Repository for RID generation.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Repository
public interface RidRepository extends BaseRepository<Rid, CenterAndMachineId> {

	/**
	 * This method fetch entity for provided center and machine id.
	 * 
	 * @param centerId
	 *            the center id.
	 * @param machineId
	 *            the machine id.
	 * @return the entity.
	 */
	@Query("from Rid r WHERE r.centerId=?1 AND r.machineId=?2")
	Rid findRid(String centerId, String machineId);

	/**
	 * This method update sequence number against provided center and machine id.
	 * 
	 * @param currentSequence
	 *            the current sequence number.
	 * @param centerId
	 *            the center id.
	 * @param machineId
	 *            the machine id.
	 * @return the number of updated rows.
	 */
	@Modifying
	@Transactional
	@Query("UPDATE Rid r SET r.currentSequenceNo=?1 WHERE r.centerId=?2 AND r.machineId=?3")
	int updateRid(int currentSequence, String centerId, String machineId);

}
