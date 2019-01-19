package io.mosip.registration.repositories;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.MachineMaster;

/**
 * The repository interface for {@link MachineMaster} entity
 * @author Yaswanth S
 * @since 1.0.0
 *
 */
public interface MachineMasterRepository extends BaseRepository<MachineMaster, String>{
	
	/**
	 * Find the station id based on macAddress
	 * @param macAddress macAddress to get {@link MachineMaster}
	 * @return
	 */
	MachineMaster findByMacAddress(String macAddress);

	
}
