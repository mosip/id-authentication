package io.mosip.registration.repositories.mastersync;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.GenderType;
import io.mosip.registration.entity.mastersync.GenderTypeId;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
public interface GenderRepostry extends BaseRepository<GenderType, GenderTypeId> {

}
