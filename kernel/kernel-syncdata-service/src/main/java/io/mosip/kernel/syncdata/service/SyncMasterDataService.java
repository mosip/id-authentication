package io.mosip.kernel.syncdata.service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import io.mosip.kernel.syncdata.dto.UploadPublicKeyRequestDto;
import io.mosip.kernel.syncdata.dto.UploadPublicKeyResponseDto;
import io.mosip.kernel.syncdata.dto.response.MasterDataResponseDto;

/**
 * Masterdata sync handler service
 * 
 * @author Abhishek Kumar
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
public interface SyncMasterDataService {

	/**
	 * 
	 * @param regCenterId      - registration center id
	 * @param macAddress       - MAC address of the machine
	 * @param serialNumber     - serial number for either desktop or dongle
	 * @param lastUpdated      - last updated time stamp
	 * @param currentTimestamp - current time stamp
	 * @return {@link MasterDataResponseDto}
	 * @throws InterruptedException - this method will throw execution exception
	 * @throws ExecutionException   -this method will throw interrupted exception
	 */
	MasterDataResponseDto syncData(String regCenterId, String macAddress, String serialNumber,
			LocalDateTime lastUpdated, LocalDateTime currentTimestamp,String keyIndex) throws InterruptedException, ExecutionException;

	/**
	 * Upload a public key to identify a machine
	 * 
	 * @param uploadPublicKeyRequestDto {@link UploadPublicKeyRequestDto} inatance
	 * @return {@link UploadPublicKeyResponseDto} instance
	 */
	UploadPublicKeyResponseDto uploadpublickey(UploadPublicKeyRequestDto uploadPublicKeyRequestDto);

}
