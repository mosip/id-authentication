package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterHistory;

/**
 * Repository class to fetch registration center history details
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterHistoryRepository extends JpaRepository<RegistrationCenterHistory, String> {

	/**
	 * Function to fetch registration center by id language code and effective time
	 * 
	 * @param id             The registration center id
	 * @param languageCode   The language code
	 * @param effectivetimes The effective time
	 * @return list of {@link RegistrationCenterHistory} -list of registration
	 *         center history
	 */
	List<RegistrationCenterHistory> findByIdAndLangCodeAndEffectivetimesLessThanEqualAndIsDeletedFalse(String id,
			String languageCode, LocalDateTime effectivetimes);

}
