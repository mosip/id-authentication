package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * DAO class for Repository
 * 
 * @author Balaji Sridharan
 * @author Mahesh Kumar
 * @author Saravanakumar Gnanaguru
 * @since 1.0.0
 *
 */
public interface RegistrationDAO {
	/**
	 * Saves the Registration entity
	 * 
	 * @param zipFileName
	 *            the name of the zip file with absolute path
	 * @param individualName
	 *            the name of the individual
	 * @throws RegBaseCheckedException
	 */
	void save(String zipFileName, String individualName) throws RegBaseCheckedException;

	/**
	 * This method updates the status of the packet
	 * 
	 * @param registrationID
	 *            the id of the {@link Registration} entity to be updated
	 * @param clientStatusCode
	 *            the status to be updated
	 * @param statusComments
	 *            the status comments to be updated
	 * 
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
	 * 
	 * This method is used to get the Packet details using the Id.
	 * 
	 * @param packetNames
	 * @return
	 */
	List<Registration> getRegistrationByStatus(List<String> packetStatus);
	/**
	 * 
	 * This method is used to update the registration status in the Registration
	 * table.
	 * 
	 * @param regId
	 * @return
	 */
	Registration updateRegStatus(Registration packetStatus);
	
	/**Fetch the packets that needs to be Synched with the server.
	 * @param statusCodes
	 * @return
	 */
	List<Registration> getPacketsToBeSynched(List<String> statusCodes);
	
	/**Update the Packet sync status in the database
	 * @param packet
	 * @return
	 */
	Registration updatePacketSyncStatus(Registration packet);
	
	/**
	 * Get all the Re-Registration packets
	 * @param status
	 * @return
	 */
	List<Registration> getAllReRegistrationPackets(String[] status);
}