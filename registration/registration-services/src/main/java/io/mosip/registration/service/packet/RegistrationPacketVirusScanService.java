package io.mosip.registration.service.packet;

import io.mosip.registration.dto.ResponseDTO;

/**
 * Service class for Packet Scan Virus
 * 
 * @author saravanakumar gnanaguru
 *
 */
public interface RegistrationPacketVirusScanService {

	/**
	 * Scan the packets in the configured path
	 * @return Success or Error Response based on the virus scan result
	 */
	ResponseDTO scanPacket();

}