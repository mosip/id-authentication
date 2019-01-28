package io.mosip.registration.repositories.mastersync;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.MasterMachineType;

/**
 * Repository to perform CRUD operations on MachineType.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface MasterSyncMachineTypeRepository extends BaseRepository<MasterMachineType, String> {

}
