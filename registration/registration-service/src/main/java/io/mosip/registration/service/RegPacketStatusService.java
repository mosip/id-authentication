package io.mosip.registration.service;

import io.mosip.registration.dto.ResponseDTO;

public interface RegPacketStatusService {

	/**
	 * packet status sync with server
	 * 
	 * @return ResponseDTO which specifies either success response or error response
	 *         after sync with server
	 */
	public ResponseDTO packetSyncStatus();
}
