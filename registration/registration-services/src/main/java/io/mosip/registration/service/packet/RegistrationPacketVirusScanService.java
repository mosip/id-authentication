package io.mosip.registration.service.packet;

import io.mosip.registration.dto.ResponseDTO;

public interface RegistrationPacketVirusScanService {

	/**
	 * Scan the packets in the configured path
	 * @return
	 */
	ResponseDTO scanPacket();

}