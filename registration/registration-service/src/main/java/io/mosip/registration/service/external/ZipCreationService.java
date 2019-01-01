package io.mosip.registration.service.external;

import java.util.Map;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface to generate the in-memory zip file for Registration Packet
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface ZipCreationService {

	/**
	 * Returns the byte array of the packet zip file containing the Registration
	 * Details
	 * 
	 * @param registrationDTO
	 *            the Registration to be stored in zip file
	 * @return the byte array of packet zip file
	 * @throws RegBaseCheckedException
	 */
	byte[] createPacket(final RegistrationDTO registrationDTO, final Map<String, byte[]> jsonMap)
			throws RegBaseCheckedException;
}
