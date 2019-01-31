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
	 * @return ResponseDTO which specifies either success response or error response
	 *         after sync with server
	 */
	public ResponseDTO packetSyncStatus();
	
	/**
	 * @return response DTO
	 */
	ResponseDTO deleteRegistrationPackets();
	/**
	 * Packet Sync Status
	 * @return response DTO
	 */
	public ResponseDTO syncPacket();
	
	/**
	 * Delete Registrations
	 * @param registrations
	 * @return response
	 */
	void deleteRegistrations(List<Registration> registrations);
	
}
