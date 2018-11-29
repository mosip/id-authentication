package io.mosip.kernel.synchandler.service;

import java.time.LocalDateTime;

import io.mosip.kernel.synchandler.dto.MasterDataResponseDto;

/**
 * masterdata sync handler service
 * 
 * @author Abhishek Kumar
 * @since 29-11-2018
 *
 */
public interface MasterDataService {
	/**
	 * method to get updated masterData
	 * 
	 * @param machineId
	 * @param lastUpdated
	 * @return {@link MasterDataResponseDto}
	 */
	MasterDataResponseDto syncData(String machineId, LocalDateTime lastUpdated);
}
