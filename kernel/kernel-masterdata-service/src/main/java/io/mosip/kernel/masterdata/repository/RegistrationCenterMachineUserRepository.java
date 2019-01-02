package io.mosip.kernel.masterdata.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachine;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserID;

/**
 * Repository class for user machine mapping
 * 
 * @author Dharmesh Khandelwal
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterMachineUserRepository
		extends BaseRepository<RegistrationCenterUserMachine, RegistrationCenterMachineUserID> {

	/**
	 * To find all data which are not previously deleted
	 * 
	 * @param cntrId
	 *            input from user
	 * @param machineId
	 *            input from user
	 * @param usrId
	 *            input from user
	 * @return {@link RegistrationCenterUserMachine}
	 */
	@Query("FROM RegistrationCenterUserMachine a WHERE a.cntrId=?1 AND a.machineId=?2 AND a.usrId=?3 and (a.isDeleted is null or a.isDeleted =false)")
	Optional<RegistrationCenterUserMachine> findAllNondeletedMappings(String cntrId, String machineId, String usrId);

}
