package io.mosip.registration.processor.packet.service;

import io.mosip.registration.processor.packet.service.dto.RegistrationDTO;
import io.mosip.registration.processor.packet.service.exception.RegBaseCheckedException;

/**
 * Class for creating the Resident Registration
 * 
 * @author Sowmya
 * 
 *
 */
public interface PacketCreationService {

	/**
	 * Creates the packet
	 * 
	 * @param registrationDTO
	 *            the enrollment data for which packet has to be created
	 * @throws RegBaseCheckedException
	 */
	byte[] create(RegistrationDTO registrationDTO) throws RegBaseCheckedException;

	String getCreationTime();
}
