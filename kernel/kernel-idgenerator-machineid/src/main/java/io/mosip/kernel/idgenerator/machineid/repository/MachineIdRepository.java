package io.mosip.kernel.idgenerator.machineid.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.machineid.entity.MachineId;

/**
 * Repository class for {@link MachineId}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Repository
public interface MachineIdRepository extends BaseRepository<MachineId, Integer> {
	/**
	 * This method triggers query to retreive the last generated machine ID.
	 * 
	 * @return the last generated machine ID.
	 */
	@Query(value = "select m.machine_id FROM ids.mid m where m.machine_id = (select max(m.machine_id) from ids.mid m) ", nativeQuery = true)
	public MachineId findMaxMachineId();
}
