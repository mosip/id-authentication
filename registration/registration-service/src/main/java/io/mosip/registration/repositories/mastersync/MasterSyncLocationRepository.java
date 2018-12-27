package io.mosip.registration.repositories.mastersync;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.MasterLocation;
/**
 * This interface is JPA repository class which interacts with database and does the CRUD function. It is 
 * extended from {@link BaseRepository}
 * @author Sreekar Chukka
 *
 */
public interface MasterSyncLocationRepository extends BaseRepository<MasterLocation, String> {
	
	

}
