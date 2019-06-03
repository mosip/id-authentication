package io.mosip.registration.repositories;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.id.RegMachineSpecId;

/**
 * The repository interface for {@link MachineMaster} entity
 * @author Yaswanth S
 * @since 1.0.0
 *
 */
public interface MachineMasterRepository extends BaseRepository<MachineMaster, RegMachineSpecId>{
	
	/**
	 * Find the station id based on macAddress.
	 *
	 * @param macAddress macAddress to get {@link MachineMaster}
	 * @return the machine master
	 */
	MachineMaster findByIsActiveTrueAndMacAddress(String macAddress);

	/**
	 * Find the {@link MachineMaster} based on active status and machine name
	 * 
	 * @param machineName
	 *            the name of the machine
	 * @return the {@link MachineMaster} entity satisfying the given criteria
	 */
	MachineMaster findByIsActiveTrueAndName(String machineName);
	
}
