/**
 *
 *
 */

package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.MachineHistory;

/**
 * Repository function to fetching machine History Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Repository
public interface MachineHistoryRepository extends BaseRepository<MachineHistory, String> {

	/**
	 * This method trigger query to fetch Machine History Details based on
	 * Machine Id, language code and effective date time
	 * 
	 * @param id
	 *            Machine History id provided by user
	 * @param langCode
	 *            language code provided by user
	 * @param effectDtimes
	 *            effective Date and time provided by user in the format "yyyy-mm-ddThh:mm:ss"
	 * @return List<MachineHistory> fetched from database
	 */
	List<MachineHistory> findByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsActiveTrueAndIsDeletedFalse(String id, String langCode,
			LocalDateTime effectDtimes);
}
