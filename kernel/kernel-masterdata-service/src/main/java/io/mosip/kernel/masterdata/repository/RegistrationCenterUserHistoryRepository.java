package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserHistoryPk;

/**
 * Repository to perform CRUD operations on RegistrationCenterUserHistory.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 * @see RegistrationCenterUserHistory
 * @see BaseRepository
 *
 */
@Repository
public interface RegistrationCenterUserHistoryRepository extends BaseRepository<RegistrationCenterUserHistory, RegistrationCenterUserHistoryPk> {

}
