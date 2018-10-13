package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * DAO class for Repository
 * 
 * @author Balaji Sridharan
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
	 * This methods is used to approve the packet
	 * 
	 * @return List of Registration Packets which are in created state.
	 */
	List<Registration> approvalList();

	/**
	 * This method updates the status of the packet
	 * 
	 * @param id
	 *            the id of the {@link Registration} entity to be updated
	 * @param clientStatus_code
	 *            the status to be updated
	 * @param approverUsrId
	 *            the user id of the approver
	 * @param statusComments
	 *            the status comments to be updated
	 * @param updBy
	 *            the user id of the user
	 * 
	 * @return the updated {@link Registration} entity
	 */
	Registration updateStatus(String id, String clientStatusCode, String approverUsrId, String statusComments,
			String updBy);

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
	List<Registration> getRegistrationById(List<String> packetNames);

	/**
	 * 
	 * This method is used to update the registration status in the Registration
	 * table.
	 * 
	 * @param regId
	 * @return
	 */
	Registration updateRegStatus(String regId);
}
