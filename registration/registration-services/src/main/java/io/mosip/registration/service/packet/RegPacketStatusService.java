package io.mosip.registration.service.packet;

import java.util.List;

import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface to update status of the registration packets based on Packet Status
 * Reader service and delete the Registration Packets based on the status of the
 * packets
 *
 * @author Himaja Dhanyamraju
 * @since 1.0.0
 */
public interface RegPacketStatusService {

	/**
	 * Updates the server status code of the packets based on the status received
	 * from Packet Status Reader Service
	 * 
	 * <p>
	 * Fetch the Registration ID of the packets with Client Status Code as PUSHED.
	 * Send these Registration IDs to the Packet Status Reader service. Based on the
	 * response status received, update the Server Status Code of the Packet.
	 * </p>
	 * 
	 * <p>
	 * Returns the {@link ResponseDTO} object.
	 * </p>
	 * 
	 * <p>
	 * If all the above processes had completed successfully,
	 * {@link SuccessResponseDTO} will be set in {@link ResponseDTO} object
	 * </p>
	 * 
	 * <p>
	 * If any exception occurs, {@link ErrorResponseDTO} will be set in
	 * {@link ResponseDTO} object
	 * </p>
	 *  
	 * @param triggerpoint
	 *            - the point by which the service was triggered.
	 *            <p>
	 *            <b>SYSTEM</b>
	 *            </p>
	 *            <p>
	 *            If service is triggered by System
	 *            </p>
	 *            <p>
	 *            <b>User ID</b>
	 *            </p>
	 *            <p>
	 *            If service is triggered by the User
	 *            </p>
	 * 
	 * @return {@link ResponseDTO} which specifies either success response or error response
	 *         after sync with server
	 * @throws RegBaseCheckedException 
	 */
	ResponseDTO packetSyncStatus(String triggerpoint) throws RegBaseCheckedException;
	
	/**
	 * Deletes the Registration Packets from the local system based on the status of
	 * the packets and the configured number of days
	 * 
	 * <p>
	 * The criteria for deleting are:
	 * </p>
	 * <ul>
	 * <li>The status of the Packet has to be <b>PROCESSED</b></li>
	 * <li>The configured number of days specifies the days after which the server
	 * status code had been updated to <b>PROCESSED</b></li>
	 * </ul>
	 * 
	 * <p>
	 * Deletes the following based
	 * </p>
	 * <ul>
	 * <li>Registration Packet and Acknowledgement Receipt from local system</li>
	 * <li>Registration data from {@link Registration} table</li>
	 * </ul>
	 * 
	 * <p>
	 * Returns the {@link ResponseDTO} object.
	 * </p>
	 * 
	 * <p>
	 * If all the above processes had completed successfully,
	 * {@link SuccessResponseDTO} will be set in {@link ResponseDTO} object
	 * </p>
	 * 
	 * <p>
	 * If any exception occurs, {@link ErrorResponseDTO} will be set in
	 * {@link ResponseDTO} object
	 * </p>
	 * 
	 * @return {@link ResponseDTO} specifying the status after deleting the
	 *         Registration Packets
	 */
	ResponseDTO deleteRegistrationPackets();

	/**
	 * Synchronizes the registration packets to the server and updates the server
	 * status code
	 * 
	 * <p>
	 * The status of the packets has to be any one of the following:
	 * </p>
	 * <ul>
	 * <li>APPROVED</li>
	 * <li>REJECTED</li>
	 * <li>RE_REGISTER_APPROVED</li>
	 * </ul>
	 * 
	 * <p>
	 * On successful sync of packet with the server through Packet Sync Service, the
	 * Server Status Code of that packet would be updated to PUSHED
	 * </p>
	 * 
	 * 
	 * <p>
	 * Returns the {@link ResponseDTO} object.
	 * </p>
	 * 
	 * <p>
	 * If all the above processes had completed successfully,
	 * {@link SuccessResponseDTO} will be set in {@link ResponseDTO} object
	 * </p>
	 * 
	 * <p>
	 * If any exception occurs, {@link ErrorResponseDTO} will be set in
	 * {@link ResponseDTO} object
	 * </p>
	 * 
	 * @param triggerpoint
	 *            - the point by which the service was triggered.
	 *            <p>
	 *            <b>SYSTEM</b>
	 *            </p>
	 *            <p>
	 *            If service is triggered by System
	 *            </p>
	 *            <p>
	 *            <b>User ID</b>
	 *            </p>
	 *            <p>
	 *            If service is triggered by the User
	 *            </p>
	 * 
	 * @return {@link ResponseDTO} which specifies either success response or error
	 *         response after sync with server
	 */
	ResponseDTO syncPacket(String triggerpoint);
	
	/**
	 * Deletes the list of {@link Registration} entries from the local system based
	 * on the status of the packets
	 * 
	 * <p>
	 * The criterion for deleting is:
	 * </p>
	 * <ul>
	 * <li>The status of the Packet has to be <b>PROCESSED</b></li>
	 * </ul>
	 * 
	 * <p>
	 * Deletes the following based
	 * </p>
	 * <ul>
	 * <li>Registration Packet and Acknowledgement Receipt from local system</li>
	 * <li>Registration data from {@link Registration} table</li>
	 * </ul>
	 * 
	 * @param registrations
	 *            the list of {@link Registration} entries to be deleted
	 */
	void deleteRegistrations(List<Registration> registrations);

	/**
	 * Deletes all the registration packets which are no more needed for the
	 * re-mapped machine
	 * 
	 * <p>
	 * The status of the registration has to be anyone of the following for
	 * deletion:
	 * </p>
	 * <ul>
	 * <li>RE-REGISTER</li>
	 * <li>PROCESSING</li>
	 * <li>processed</li>
	 * </ul>
	 * 
	 * <p>
	 * Deletes the following based
	 * </p>
	 * <ul>
	 * <li>Registration Packet and Acknowledgement Receipt from local system</li>
	 * <li>Registration data from {@link Registration} table</li>
	 * </ul>
	 */
	void deleteAllProcessedRegPackets();
	
}
