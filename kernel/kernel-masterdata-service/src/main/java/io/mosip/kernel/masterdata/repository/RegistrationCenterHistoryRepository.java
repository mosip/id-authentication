package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterHistory;

/**
 * Repository class to fetch registration center history details
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterHistoryRepository extends BaseRepository<RegistrationCenterHistory, String> {

	/**
	 * Function to fetch registration center by id language code and effective time
	 * 
	 * @param id
	 *            The registration center id
	 * @param languageCode
	 *            The language code
	 * @param effectivetimes
	 *            The effective time
	 * @return Registration center history
	 */
	@Query("FROM RegistrationCenterHistory WHERE id =?1 and languageCode =?2 and effectivetimes <=?3 and (isDeleted is null or isDeleted =false)")
	List<RegistrationCenterHistory> findByIdAndLanguageCodeAndEffectivetimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
			String id, String languageCode, LocalDateTime effectivetimes);

}
