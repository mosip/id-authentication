package io.mosip.registration.service.packet;

import io.mosip.registration.dto.ResponseDTO;

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
	
}
