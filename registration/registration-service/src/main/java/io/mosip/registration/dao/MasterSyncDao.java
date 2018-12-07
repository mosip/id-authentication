package io.mosip.registration.dao;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.mastersync.MasterSyncDto;
import io.mosip.registration.dto.mastersync.MasterSyncResponseDto;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * The Interface MasterSyncDao.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
public interface MasterSyncDao {

	/**
	 * Gets the master sync status.
	 *
	 * @return the master sync status
	 */
	public SyncControl getMasterSyncStatus(String synccontrol);

	/**
	 * inserting master sync data into the database using entity.
	 *
	 * @param masterSyncDto the master sync dto
	 * @throws RegBaseCheckedException the reg base checked exception
	 */
	public void insertMasterSyncData(MasterSyncDto masterSyncDto) throws RegBaseCheckedException;

}
