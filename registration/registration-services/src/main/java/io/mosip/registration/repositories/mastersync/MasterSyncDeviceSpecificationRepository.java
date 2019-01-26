package io.mosip.registration.repositories.mastersync;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.MasterDeviceSpecification;

/**
 * 
 * Repository function to fetching and save device specification details
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface MasterSyncDeviceSpecificationRepository extends BaseRepository<MasterDeviceSpecification, String> {
	
}
