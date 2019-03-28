package io.mosip.registration.dao;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * DAO class for Registration Repository.
 *
 * @author Balaji Sridharan
 * @author Mahesh Kumar
 * @author Saravanakumar Gnanaguru
 * @since 1.0.0
 */
public interface RegistrationDAO {

	/**
	 * Saves the Registration entity.
	 *
	 * @param zipFileName            
	 * 				the name of the zip file with absolute path
	 * @param registrationDTO            
	 * 				the {@link RegistrationDTO} of the individual
	 * @throws RegBaseCheckedException             
	 * 				will be thrown if any exception occurs while saving {@link Registration}
	 */
	void save(String zipFileName, RegistrationDTO registrationDTO) throws RegBaseCheckedException;

	/**
	 * This method updates the status of the packet.
	 *
	 * @param registrationID            
	 * 				the id of the {@link Registration} entity to be updated
	 * @param statusComments            
	 * 				the status comments to be updated
	 * @param clientStatusCode            
	 * 				the status to be updated
	 * @return the updated {@link Registration} entity
	 */
	Registration updateRegistration(String registrationID,String statusComments,String clientStatusCode);

	/**
	 * This method retrieves the list of Registrations by status.
	 * 
	 * @param status
	 *            the status of the registration to be retrieved
	 * @return the list of {@link Registration} based on the given input status
	 */
	List<Registration> getEnrollmentByStatus(String status);

	/**
	 * This method is used to get the Packet details using the Id.
	 *
	 * @param packetStatus 
	 * 				the packet status
	 * @return the list of {@link Registration} based on status
	 */
	List<Registration> getRegistrationByStatus(List<String> packetStatus);

	/**
	 * This method is used to update the registration status in the Registration
	 * table.
	 *
	 * @param packetStatus 
	 * 				the packet status
	 * @return the registration
	 */
	Registration updateRegStatus(PacketStatusDTO packetStatus);
	
	/**
	 * Fetch the packets that needs to be Synched with the server.
	 *
	 * @param statusCodes 
	 * 				the status codes
	 * @return the packets to be synched
	 */
	List<Registration> getPacketsToBeSynched(List<String> statusCodes);
	
	/**
	 * Update the Packet sync status in the database.
	 *
	 * @param packet 
	 * 				the packet
	 * @return the registration
	 */
	Registration updatePacketSyncStatus(PacketStatusDTO packet);
	
	/**
	 * Get all the Re-Registration packets.
	 *
	 * @param status 
	 * 				the status
	 * @return the all re registration packets
	 */
	List<Registration> getAllReRegistrationPackets(String[] status);

	/**
	 * Gets the registration by id.
	 *
	 * @param clientStatusCode 
	 * 				the client status code
	 * @param rId 			
	 * 				the registration id
	 * @return the registration by id
	 */
	Registration getRegistrationById(String clientStatusCode,String rId);

	/**
	 * Get Registration.
	 *
	 * @param regIds            
	 * 				id
	 * @return List of Registrations
	 */
	List<Registration> get(List<String> regIds);
	
	/**
	 * Find by CrDtimes and client status code.
	 *
	 * @param crDtimes 
	 * 				the date upto packets to be deleted
	 * @param clientStatus 
	 * 				status of resgistrationPacket
	 * @return list of registrations
	 */
	List<Registration> get(Timestamp crDtimes, String clientStatus);

	/**
	 * Find by server status code in.
	 *
	 * @param serveSstatusCodes 
	 * 				the serve sstatus codes
	 * @return the list
	 */
	List<Registration> findByServerStatusCodeIn(List<String> serveSstatusCodes);

	/**
	 * Find by server status code not in.
	 *
	 * @param serverStatusCodes 
	 * 				the server status codes
	 * @return the list
	 */
	List<Registration> findByServerStatusCodeNotIn(List<String> serverStatusCodes);
	
	/**
	 * This method is used to fetch the records for the packets to Upload.
	 *
	 * @param clientStatus 
	 * 				the client status
	 * @param serverStatus 
	 * 				the server status
	 * @return the list
	 */
	List<Registration> fetchPacketsToUpload(List<String> clientStatus, String serverStatus);


}