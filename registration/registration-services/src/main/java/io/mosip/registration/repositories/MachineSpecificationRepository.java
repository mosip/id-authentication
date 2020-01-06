package io.mosip.registration.repositories;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegMachineSpec;
import io.mosip.registration.entity.id.RegMachineSpecId;

/**
 * Repository to perform CRUD operations on MachineSpecification.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

public interface MachineSpecificationRepository extends BaseRepository<RegMachineSpec, RegMachineSpecId> {

}
