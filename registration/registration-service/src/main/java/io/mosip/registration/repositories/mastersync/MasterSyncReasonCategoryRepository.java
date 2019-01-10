package io.mosip.registration.repositories.mastersync;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.MasterReasonCategory;

/**
 * The Interface MasterSyncReasonCategoryRepository.
 *
 * @author Sreekar Chukka
 */
@Repository
public interface MasterSyncReasonCategoryRepository extends BaseRepository<MasterReasonCategory, String> {

	List<MasterReasonCategory> findReasonCategoryByIsDeletedFalseOrIsDeletedIsNull();

}
