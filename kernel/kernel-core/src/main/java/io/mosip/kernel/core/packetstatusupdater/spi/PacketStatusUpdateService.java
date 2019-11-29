package io.mosip.kernel.core.packetstatusupdater.spi;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.packetstatusupdater.dto.PacketStatusUpdateDto;

/**
 * The Interface PacketStatusUpdateService.
 */
@Service
public interface PacketStatusUpdateService {

	/**
	 * Gets the status.
	 *
	 * @param rid
	 *            the rid
	 * @return the status
	 */
	public PacketStatusUpdateDto getStatus(String rid);
}
