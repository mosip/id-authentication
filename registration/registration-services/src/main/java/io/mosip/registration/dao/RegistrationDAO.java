package io.mosip.registration.dao;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * This class will be used to fetch/Add/Update details from the
 * {@link Registration} and {@link RegistrationTransaction} while creating a
 * registration packet the respective packet data will be stored tables. It will
 * insert a record while creating a new packet. While performing EOD operation
 * this will fetch the registration data and updates the respective operation
 * related information to registration table. While performing the Sync and
 * Upload operations it fetches the corresponding registrations and updates the
 * registrations accordingly depending upon the status received.
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
	 * @param zipFileName     the name of the zip file with absolute path
	 * @param registrationDTO the {@link RegistrationDTO} of the individual
	 * @throws RegBaseCheckedException will be thrown if any exception occurs while
	 *                                 saving {@link Registration}
	 */
	void save(String zipFileName, RegistrationDTO registrationDTO) throws RegBaseCheckedException;

	/**
	 * <p>
	 * Once the packet gets created the packet will gets approved by the supervisor
	 * and the same will be updated as either Approved or Rejected using this
	 * method.
	 * </p>
	 * 
	 * <p>
	 * If Approved:
	 * </p>
	 * <p>
	 * The same will updated in the status comments
	 * </p>
	 * <p>
	 * If rejected:
	 * </p>
	 * <p>
	 * The rejection reason will be updated in the status comments
	 * </p>
	 *
	 * @param registrationID   the id of the {@link Registration} entity to be
	 *                         updated
	 * @param statusComments   the status comments to be updated
	 * @param clientStatusCode the status to be updated
	 * @return the updated {@link Registration} entity
	 */
	Registration updateRegistration(String registrationID, String statusComments, String clientStatusCode);

	/**
	 * <p>
	 * It will retrieve registration records based on the status
	 * </p>
	 * <p>
	 * The records will be arranged in the ascending order of the created Date time
	 * </p>
	 * 
	 * @param status the status of the registration to be retrieved
	 * @return the list of {@link Registration} based on the given input status
	 */
	List<Registration> getEnrollmentByStatus(String status);

	/**
	 * <p>
	 * This method is used to fetch the records in which the corresponding packets
	 * are ready to upload
	 * </p>
	 * <p>
	 * The records that are fetched here are based on the client status code and
	 * server status code
	 * </p>
	 * Client Status Codes
	 * <ol>
	 * <li>Synced</li>
	 * <li>Exported</li>
	 * </ol>
	 * Server Status Code
	 * <ol>
	 * <li>Resend</li>
	 * </ol>
	 * 
	 * @param packetStatus the packet status
	 * @return the list of {@link Registration} based on status
	 */
	List<Registration> getRegistrationByStatus(List<String> packetStatus);

	/**
	 * <p>
	 * Once the Packet Upload is done to the Server the same needs to be updated in
	 * the DB and it will be done through this method.
	 * </p>
	 * <p>
	 * The packet upload count will get increased and updated in the Upload count
	 * column, incase if we tries to upload the same packet again.
	 * </p>
	 * 
	 * @param packetStatus - the {@link PacketStatusDTO} to be updated
	 * @return {@link Registration} entity
	 */
	Registration updateRegStatus(PacketStatusDTO packetStatus);

	/**
	 * <p>
	 * This method will fetch the records which are in
	 * Approved/Rejected/ReRegisterApproved
	 * </p>
	 * <p>
	 * The records will be fetched in the Ascending order of Update Timestamp
	 * </p>
	 *
	 * @param statusCodes the status codes - Approved/Rejected/ReRegisterApproved
	 * @return List of {@link Registration} entities
	 */
	List<Registration> getPacketsToBeSynched(List<String> statusCodes);

	/**
	 * This method is used to update the Packet sync status in the database.
	 *
	 * @param packet the packet
	 * @return the registration
	 */
	Registration updatePacketSyncStatus(PacketStatusDTO packet);

	/**
	 * This method is used to get all the Re-Registration packets.
	 *
	 * @param status the status
	 * @return the all re registration packets
	 */
	List<Registration> getAllReRegistrationPackets(String[] status);

	/**
	 * This method is used to get the registration by id.
	 *
	 * @param clientStatusCode the client status code
	 * @param rId              the registration id
	 * @return the registration by id
	 */
	Registration getRegistrationById(String clientStatusCode, String rId);

	/**
	 * This method is used to get list of Registrations by passing list of registration id's.
	 *
	 * @param regIds id
	 * @return List of Registrations
	 */
	List<Registration> get(List<String> regIds);

	/**
	 * This method is used to find list of registrations by CrDtimes and client status code.
	 *
	 * @param crDtimes     the date upto packets to be deleted
	 * @param clientStatus status of resgistrationPacket
	 * @return list of registrations
	 */
	List<Registration> get(Timestamp crDtimes, String clientStatus);

	/**
	 * This method is used to find list of registrations by server status code in.
	 *
	 * @param serveSstatusCodes the serve status codes
	 * @return the list
	 */
	List<Registration> findByServerStatusCodeIn(List<String> serveSstatusCodes);

	/**
	 * This method is used to find list of registrations by server status code not in.
	 *
	 * @param serverStatusCodes the server status codes
	 * @return the list
	 */
	List<Registration> findByServerStatusCodeNotIn(List<String> serverStatusCodes);

	/**
	 * This method is used to fetch the records for the packets to Upload.
	 *
	 * @param clientStatus the client status
	 * @param serverStatus the server status
	 * @return the list
	 */
	List<Registration> fetchPacketsToUpload(List<String> clientStatus, String serverStatus);

}