package io.mosip.admin.packetstatusupdater.service;

import io.mosip.admin.packetstatusupdater.dto.PacketStatusUpdateResponseDto;

/**
 * The Interface PacketStatusUpdateService.
 * @author Srinivasan
 */

public interface PacketStatusUpdateService {

	/**
	 * Gets the status.
	 *
	 * @param rid
	 *            the rid
	 * @return the status
	 */
	public PacketStatusUpdateResponseDto getStatus(String rid);
}
