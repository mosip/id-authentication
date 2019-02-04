package io.mosip.registration.repositories.mastersync;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.MasterDeviceType;

/**
 * Repository function to fetching Device Type details
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface MasterSyncDeviceTypeRepository extends BaseRepository<MasterDeviceType, String> {

}