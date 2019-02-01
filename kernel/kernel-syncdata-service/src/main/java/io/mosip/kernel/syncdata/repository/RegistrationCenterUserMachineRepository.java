package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUserMachine;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineUserID;

/**
 * Repository class for center user machine mapping
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterUserMachineRepository
		extends BaseRepository<RegistrationCenterUserMachine, RegistrationCenterMachineUserID> {

	@Query("FROM RegistrationCenterUserMachine rcum WHERE rcum.cntrId=?1 AND (rcum.createdDateTime > ?2 OR rcum.updatedDateTime > ?2 OR rcum.deletedDateTime > ?2)")
	List<RegistrationCenterUserMachine> findAllByRegistrationCenterIdCreatedUpdatedDeleted(String regId,
			LocalDateTime lastUpdated);

	@Query("FROM RegistrationCenterUserMachine rcum WHERE rcum.cntrId=?1")
	List<RegistrationCenterUserMachine> findAllByRegistrationCenterId(String regId);

}
