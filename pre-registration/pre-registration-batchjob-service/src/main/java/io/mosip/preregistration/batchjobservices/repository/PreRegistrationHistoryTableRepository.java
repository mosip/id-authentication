package io.mosip.preregistration.batchjobservices.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.batchjobservices.entity.PreRegistrationHistoryTable;

/**
 * @author M1043008
 *
 */
@Repository("preRegistrationHistoryTableRepository")
public interface PreRegistrationHistoryTableRepository extends BaseRepository<PreRegistrationHistoryTable, String>{

}
