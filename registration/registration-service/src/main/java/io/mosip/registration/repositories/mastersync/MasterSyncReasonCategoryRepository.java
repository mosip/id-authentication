package io.mosip.registration.repositories.mastersync;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.MasterReasonCategory;

/**
 * 
 * @author Srinivasan
 *
 */
@Repository
public interface MasterSyncReasonCategoryRepository extends BaseRepository<MasterReasonCategory, String> {

	

}
