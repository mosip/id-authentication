package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistory;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserID;

/**
 * Repository class for user machine mapping
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterUserMachineHistoryRepository
		extends
			BaseRepository<RegistrationCenterUserMachineHistory, RegistrationCenterMachineUserID> {
	/**
	 * This method trigger query to fetch registration centers based on center
	 * id,user id,machine id and effective date
	 * 
	 * @param id
	 *            composite key consisting center id,user id,machine id
	 * @param effectivetimes
	 *            effective time as provided by user
	 * @return List of {@link RegistrationCenterUserMachineHistory} fetched by query
	 */
	List<RegistrationCenterUserMachineHistory> findByIdAndEffectivetimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
			RegistrationCenterMachineUserID id, LocalDateTime effectivetimes);
}
