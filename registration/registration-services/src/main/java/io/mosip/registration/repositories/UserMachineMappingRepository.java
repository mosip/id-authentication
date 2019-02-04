package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.UserMachineMappingID;

/**
 * The reposistory interface for {@link UserMachineMapping} entity
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */

public interface UserMachineMappingRepository extends BaseRepository<UserMachineMapping, UserMachineMappingID> {
	
	List<UserMachineMapping>findByUserMachineMappingIdMachineID(String machineId);

}
