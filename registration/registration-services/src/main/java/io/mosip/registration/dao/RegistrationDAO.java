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
	 * <p>
	 * Once the packet gets created and stored in the local this method will get
	 * called to save the record for that particular registration id.
	 * </p>
	 * 
	 * <p>
	 * All the packet related details will be stored in the Registration entity and
	 * it will get saved
	 * </p>
	 *
	 * @param zipFileName
	 *            the name of the zip file with absolute path
	 * @param registrationDTO
	 *            the {@link RegistrationDTO} of the individual
	 * @throws RegBaseCheckedException
	 *             will be thrown if any exception occurs while saving
	 *             {@link Registration}
	 */
	void save(String zipFileName, RegistrationDTO registrationDTO) throws RegBaseCheckedException;

	/**
	 * <p>
	 * Once the packet gets created the packet will gets approved by the supervisor
	 * and the same will be updated as either Approved or Rejected using this method.
	 * </p>
	 * 
	 * <p>If Approved:</p>
	 * 		<p>The same will updated in the status comments</p>
	 * <p>If rejected:</p>
	 * 		<p> The rejection reason will be updated in the status comments</p>
	 *
	 * @param registrationID
	 *            the id of the {@link Registration} entity to be updated
	 * @param statusComments
	 *            the status comments to be updated
	 * @param clientStatusCode
	 *            the status to be updated
	 * @return the updated {@link Registration} entity
	 */
	Registration updateRegistration(String registrationID, String statusComments, String clientStatusCode);

	/**
	 * <p>It will retrieve registration records based on the  status<p>
	 * <p>The records will be arranged in the ascending order of the created Date time</p>
	 * 
	 * @param status
	 *            the status of the registration to be retrieved
	 * @return the list of {@link Registration} based on the given input status
	 */
	List<Registration> getEnrollmentByStatus(String status);

	/**
	 * <p>This method is used to fetch the records in which the corresponding packets are ready to upload</p>
	 *<p>The records that are fetched here are based on the client status code and server status code</p>
	 *<ol>Client Status Codes</ol>
	 *	<li>Synced</li>
	 *	<li>Exported</li>
	 *	<ol>Server Status Code</ol>
	 *	<li>Resend</li>
	 * @param packetStatus
	 *            the packet status
	 * @return the list of {@link Registration} based on status
	 */
	List<Registration> getRegistrationByStatus(List<String> packetStatus);

	
	/**
	 * <p>Once the Packet Upload is done to the Server the same needs to be updated 
	 * in the DB and it will be done through this method.</p>
	 * <p>The packet upload count will get increased and updated in the Upload count column, incase if we 
	 * tries to upload the same packet again.</p>
	 * @param packetStatus
	 * @return {@link Registration} entity
	 */
	Registration updateRegStatus(PacketStatusDTO packetStatus);

	/**
	 *<p> This method will fetch the records which are in Approved/Rejected/ReRegisterApproved</p>
	 *<p>The records will be fetched in the Ascending order of Update Timestamp </p>
	 *
	 * @param statusCodes
	 *            the status codes - Approved/Rejected/ReRegisterApproved
	 * @return List of {@link Registration} entities
	 */
	List<Registration> getPacketsToBeSynched(List<String> statusCodes);

	/**
	 * Update the Packet sync status in the database.
	 *
	 * @param packet
	 *            the packet
	 * @return the registration
	 */
	Registration updatePacketSyncStatus(PacketStatusDTO packet);

	/**
	 * Get all the Re-Registration packets.
	 *
	 * @param status
	 *            the status
	 * @return the all re registration packets
	 */
	List<Registration> getAllReRegistrationPackets(String[] status);

	/**
	 * Gets the registration by id.
	 *
	 * @param clientStatusCode
	 *            the client status code
	 * @param rId
	 *            the registration id
	 * @return the registration by id
	 */
	Registration getRegistrationById(String clientStatusCode, String rId);

	/**
	 * Get Registration.
	 *
	 * @param regIds
	 *            id
	 * @return List of Registrations
	 */
	List<Registration> get(List<String> regIds);

	/**
	 * Find by CrDtimes and client status code.
	 *
	 * @param crDtimes
	 *            the date upto packets to be deleted
	 * @param clientStatus
	 *            status of resgistrationPacket
	 * @return list of registrations
	 */
	List<Registration> get(Timestamp crDtimes, String clientStatus);

	/**
	 * Find by server status code in.
	 *
	 * @param serveSstatusCodes
	 *            the serve sstatus codes
	 * @return the list
	 */
	List<Registration> findByServerStatusCodeIn(List<String> serveSstatusCodes);

	/**
	 * Find by server status code not in.
	 *
	 * @param serverStatusCodes
	 *            the server status codes
	 * @return the list
	 */
	List<Registration> findByServerStatusCodeNotIn(List<String> serverStatusCodes);

	/**
	 * This method is used to fetch the records for the packets to Upload.
	 *
	 * @param clientStatus
	 *            the client status
	 * @param serverStatus
	 *            the server status
	 * @return the list
	 */
	List<Registration> fetchPacketsToUpload(List<String> clientStatus, String serverStatus);

}