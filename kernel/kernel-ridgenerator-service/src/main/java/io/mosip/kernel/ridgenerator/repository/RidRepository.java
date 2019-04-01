package io.mosip.kernel.ridgenerator.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.ridgenerator.entity.Rid;
import io.mosip.kernel.ridgenerator.entity.id.CenterAndMachineId;

@Repository
public interface RidRepository extends BaseRepository<Rid, CenterAndMachineId> {

	@Query("from Rid r WHERE r.centerId=?1 AND r.machineId=?2")
	Rid findRid(String centerId, String machineId);

	@Modifying
	@Transactional
	@Query("UPDATE Rid r SET r.currentSequenceNo=?1 WHERE r.centerId=?2 AND r.machineId=?3")
	int updateRid(int currentSequence, String centerId, String machineId);

}
