package io.mosip.registration.repositories.mastersync;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.MasterMachine;

/**
 * Repository to perform CRUD operations on Machine.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */

public interface MasterSyncMachineRepository extends BaseRepository<MasterMachine, String> {
	
}
