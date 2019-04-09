package io.mosip.registration.service.packet;

import java.util.List;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.Registration;

/**
 * {@code RegPacketStatusService} is the Registration Packet Status Service interface
 *
 * @author Himaja Dhanyamraju
 */
public interface RegPacketStatusService {

	/**
	 * packet status sync with server
	 * 
	 * @param triggerpoint - the point by which the service was triggered
	 * 
	 * @return ResponseDTO which specifies either success response or error response
	 *         after sync with server
	 */
	public ResponseDTO packetSyncStatus(String triggerpoint);
	
	/**
	 * @return response DTO
	 */
	ResponseDTO deleteRegistrationPackets();
	
	/**
	 * This method will sync the packets to the server
	 * @param triggerpoint The trigger point for webservice call
	 * @return Returning the response as dto
	 */
	public ResponseDTO syncPacket(String triggerpoint);
	
	/**
	 * Delete Registrations
	 * @param registrations
	 * 			- the list of registrations that are to be deleted
	 */
	void deleteRegistrations(List<Registration> registrations);

	/**
	 * deletes all the packets which is no more needed for the remapped machine
	 */
	void deleteAllProcessedRegPackets();
	
}
