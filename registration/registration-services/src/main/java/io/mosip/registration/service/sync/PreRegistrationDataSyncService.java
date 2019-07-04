package io.mosip.registration.service.sync;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.PreRegistrationList;

/**
 * It interfaces with external pre-registration data sync services and download the packets based on the date range and packet id then  
 * store it into the local machine in encrypted format. 
 * It also maintain the records in local database along with the key used for encryption. 
 * 
 * This is invoked from job scheduler and new registration demographic screen.  
 * Job scheduler - download the pre-registration packets between the date range based on value configured in the properties. 
 * New Registration screen - download a particular packet from MOSIP server if online connectivity exists, otherwise use the packet from local file system.   
 * 
 * @author YASWANTH S
 * @since 1.0.0
 */
public interface PreRegistrationDataSyncService {

	/**
	 * Retrieve pre-registration packet ids from pre-reg server for the given center id and date range. 
	 * Then download the packet specific to id from pre-reg server and update the database and local file system. 
	 * 
	 * @param syncJobId
	 *            the job which it triggered
	 * @return ResponseDTO response data
	 * 			success or failure object that holds the error detail. 
	 */
	public ResponseDTO getPreRegistrationIds(String syncJobId);

	/**
	 * Get Pre Registration packet from either MOSIP pre-reg server or reg client db/ file system. 
	 * If online connectivity exists, then always download the latest one from the server. 
	 * If no connectivity then uses the packet which was already downloaded into the system through sync jobs. 
	 * It decrypts the packets, after fetching from file system using the key [session key] available in the local db. 
	 * 
	 * @param preRegistrationId
	 *            preRegId
	 * @return ResponseDTO response data
	 * 				Pre-registration packet data. 
	 */
	public ResponseDTO getPreRegistration(String preRegistrationId);

	/**
	 * Fetch all the Pre-Registration Records from db that needs to be deleted based on appointment date and no. of configured days.  If 
	 * today date is greater than appointment date plus no. of configured days then delete those records.
	 * 
	 * @return ResponseDTO - holds response data
	 */
	public ResponseDTO fetchAndDeleteRecords();

	/**
	 * Delete pre registration records from database and file system based on the preregistration packet id provided in the input. 
	 * @param responseDTO
	 * 			Object to hold the response data. 
	 * @param preRegList
	 * 			List of pre-registration id, which is to be deleted. 
	 */
	public void deletePreRegRecords(ResponseDTO responseDTO, final List<PreRegistrationList> preRegList);
	
	/**
	 * Gets the pre registration record for deletion.
	 *
	 * @param preRegistrationId 
	 * 				the pre registration id
	 * @return the pre registration record for deletion
	 */
	public PreRegistrationList getPreRegistrationRecordForDeletion(String preRegistrationId);
	
	
	/**
	 * Last pre registration packet downloaded date time.
	 *
	 * @return the timestamp
	 */
	public Timestamp getLastPreRegPacketDownloadedTime();
}
